package com.healthcare.healthcare_app.users.service;

import com.healthcare.healthcare_app.doctor.entity.Doctor;
import com.healthcare.healthcare_app.doctor.repository.DoctorRepository;
import com.healthcare.healthcare_app.exceptions.BadRequestException;
import com.healthcare.healthcare_app.exceptions.NotFoundException;
import com.healthcare.healthcare_app.notification.dto.NotificationDTO;
import com.healthcare.healthcare_app.notification.service.NotificationService;
import com.healthcare.healthcare_app.patient.entity.Patient;
import com.healthcare.healthcare_app.patient.repository.PatientRepository;
import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.role.entity.Role;
import com.healthcare.healthcare_app.role.repository.RoleRepository;
import com.healthcare.healthcare_app.security.JwtService;
import com.healthcare.healthcare_app.users.dto.LoginRequest;
import com.healthcare.healthcare_app.users.dto.LoginResponse;
import com.healthcare.healthcare_app.users.dto.RegistrationRequest;
import com.healthcare.healthcare_app.users.dto.ResetPasswordRequest;
import com.healthcare.healthcare_app.users.entity.PasswordResetCode;
import com.healthcare.healthcare_app.users.entity.User;
import com.healthcare.healthcare_app.users.repository.PasswordResetRepository;
import com.healthcare.healthcare_app.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService{

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final NotificationService notificationService;
    private final PatientRepository patientRepository;
    private final DoctorRepository doctorRepository;
    private final PasswordResetRepository passwordResetRepository;
    private final CodeGenerator codeGenerator;

    @Value("${login.link}")
    private String loginLink;
    @Value("${password.reset.link}")
    private String resetLink;

    @Override
    public Response<String> register(RegistrationRequest registrationRequest) {

        if (isPresent(registrationRequest)){
            throw new BadRequestException("User with email::"+registrationRequest.getEmail()+" already exists");
        }

        List<String> requestedRoleNames =(registrationRequest.getRoles() != null && !registrationRequest.getRoles().isEmpty())
                ? registrationRequest.getRoles().stream().map(String::toUpperCase).toList()
                : List.of("PATIENT");

        boolean isDoctor = requestedRoleNames.contains("DOCTOR");

        if (isDoctor && (registrationRequest.getLicenseNumber() == null || registrationRequest.getLicenseNumber().isBlank())){

            throw new BadRequestException("License number required to register a doctor");
        }

        //load and validate roles from data base

        Set<Role> roles = requestedRoleNames.stream()
                .map(roleRepository::findByName)
                .flatMap(Optional::stream)
                .collect(Collectors.toSet());

        if (roles.isEmpty()){
            throw new NotFoundException("Registration failed: Requested roles were not found in the database");
        }

        User newUser = User.builder()
                .name(registrationRequest.getName())
                .email(registrationRequest.getEmail())
                .password(passwordEncoder.encode(registrationRequest.getPassword()))
                .roles(roles)
                .build();

        User savedUser = userRepository.save(newUser);

        log.info("New user registered: {} with {} roles",savedUser.getEmail(),roles.size());

        for (Role role : roles){
            String roleName = role.getName();

            switch (roleName){
                case "PATIENT":
                    createPatientProfile(savedUser);
                    log.info("Patient profile created: {}",savedUser.getEmail());
                    break;
                case "DOCTOR":
                    createDoctorProfile(registrationRequest,savedUser);
                    log.info("Doctor profile created: {}",savedUser.getEmail());
                    break;
                case "ADMIN":
                    log.info("Admin profile created: {}",savedUser.getEmail());
                    break;
                default:
                    log.warn("Assigned role '{}' has no corresponding profile creation logic",roleName);

            }

        }

        sendRegistrationEmail(registrationRequest,savedUser);

        return Response.<String>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Registration successful. A welcome email has been send to you")
                .data(savedUser.getEmail())
                .build();


    }

    private void sendRegistrationEmail(RegistrationRequest registrationRequest, User savedUser) {
        NotificationDTO welcomeEmail = NotificationDTO.builder()
                .recipient(savedUser.getEmail())
                .subject("Welcome to DAT Health!")
                .templateName("welcome")
                .message("Thank you for registering your account is ready")
                .templateVariables(Map.of(
                        "name",registrationRequest.getName(),
                        "loginLink",loginLink
                ))
                .build();

        notificationService.sendEmail(welcomeEmail,savedUser);

    }

    private void createDoctorProfile(RegistrationRequest registrationRequest, User savedUser) {

        Doctor doctor = Doctor.builder()
                .specialization(registrationRequest.getSpecialization())
                .licenseNumber(registrationRequest.getLicenseNumber())
                .user(savedUser)
                .build();

        doctorRepository.save(doctor);

        log.info("Doctor profile created");

    }


    private void createPatientProfile( User savedUser) {

        Patient patient = Patient.builder()
                .user(savedUser)
                .build();
        patientRepository.save(patient);
        log.info("Patient profile created");
    }

    private boolean isPresent(RegistrationRequest registrationRequest) {
        return userRepository.findByEmail(registrationRequest.getEmail()).isPresent();
    }

    @Override
    public Response<LoginResponse> login(LoginRequest loginRequest) {

        String email = loginRequest.getEmail();
        String password = loginRequest.getPassword();

        User user = userRepository.findByEmail(email).orElseThrow(()-> new NotFoundException("Email not found"));

        if (!passwordEncoder.matches(password,user.getPassword())){
            throw new BadRequestException("Password doesn't match");
        }

        String token = jwtService.generateToken(email);

        LoginResponse loginResponse = LoginResponse.builder()
                .roles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()))
                .token(token)
                .build();
        return Response.<LoginResponse>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Login successful")
                .data(loginResponse)
                .build();
    }

    @Override
    public Response<?> forgetPassword(String email) {

        User user = userRepository.findByEmail(email).orElseThrow(()-> new NotFoundException("User not found"));
        passwordResetRepository.deleteByUserId(user.getId());

        String code = codeGenerator.generateUniqueCode();

        PasswordResetCode resetCode = PasswordResetCode.builder()
                .user(user)
                .code(code)
                .expiryDate(calculateExpiryDate())
                .used(false)
                .build();
        passwordResetRepository.save(resetCode);

        NotificationDTO passwordResetEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password Reset Code")
                .templateName("password-reset")
                .templateVariables(Map.of(
                        "name",user.getName(),
                        "resetLink",resetLink + code
                ))
                .build();

        notificationService.sendEmail(passwordResetEmail,user);



        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password reset code sent to your email")
                .build();
    }

    private LocalDateTime calculateExpiryDate() {
        return LocalDateTime.now().plusHours(5);
    }

    @Override
    public Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest) {
        String code = resetPasswordRequest.getCode();
        String newPassword = resetPasswordRequest.getNewPassword();

      PasswordResetCode resetCode =  passwordResetRepository.findByCode(code).orElseThrow(()-> new BadRequestException("Invalid reset code"));

      //Check code expiration
        if (resetCode.getExpiryDate().isBefore(LocalDateTime.now())){
            passwordResetRepository.delete(resetCode);
            throw new BadRequestException("Reset code has expired");
        }

        //update password
        User user = resetCode.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        //delete code after successful use
        passwordResetRepository.delete(resetCode);


        NotificationDTO passwordUpdateEmail = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password updated successfully")
                .templateName("password-update-confirmation")
                .templateVariables(Map.of(
                        "name",user.getName()
                ))
                .build();

        notificationService.sendEmail(passwordUpdateEmail,user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password updated successfully")
                .build();
    }
}
