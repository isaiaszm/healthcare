package com.healthcare.healthcare_app.users.service;

import com.healthcare.healthcare_app.exceptions.BadRequestException;
import com.healthcare.healthcare_app.exceptions.NotFoundException;
import com.healthcare.healthcare_app.notification.dto.NotificationDTO;
import com.healthcare.healthcare_app.notification.service.NotificationService;
import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.users.dto.UpdatePasswordRequest;
import com.healthcare.healthcare_app.users.dto.UserDTO;
import com.healthcare.healthcare_app.users.entity.User;
import com.healthcare.healthcare_app.users.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;
    private final String uploadDir = "uploads/profile-pictures/";

    @Override
    public User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            throw new NotFoundException("User is not authenticated");
        }

        String email = authentication.getName();

        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    public Response<UserDTO> getMyUserDetails() {

        User user = getCurrentUser();

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User details retrieve successfully")
                .data(userDTO)
                .build();

    }

    @Override
    public Response<UserDTO> getUserById(Long id) {

        User user = userRepository.findById(id).orElseThrow(() -> new NotFoundException("User not found"));

        UserDTO userDTO = modelMapper.map(user, UserDTO.class);

        return Response.<UserDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("User retrieve successfully")
                .data(userDTO)
                .build();

    }

    @Override
    public Response<List<UserDTO>> getAllUsers() {

        List<UserDTO> userDTOS = userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserDTO.class))
                .toList();

        return Response.<List<UserDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Users retrieve successfully")
                .data(userDTOS)
                .build();

    }

    @Override
    public Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest) {

        User user = getCurrentUser();

        String newPassword = updatePasswordRequest.getNewPassword();
        String oldPassword = updatePasswordRequest.getOldPassword();

        if (oldPassword == null || newPassword == null) {
            throw new BadRequestException("Old and new password are required");
        }

        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new BadRequestException("Old password it not correct");
        }

        user.setPassword(passwordEncoder.encode(newPassword));

        userRepository.save(user);


        NotificationDTO passwordUpdateEmailDTO = NotificationDTO.builder()
                .recipient(user.getEmail())
                .subject("Password updated successfully")
                .templateName("password-change")
                .templateVariables(Map.of(
                        "name", user.getName()
                ))
                .build();

        notificationService.sendEmail(passwordUpdateEmailDTO, user);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Password updated successfully")
                .build();


    }

    @Override
    public Response<?> uploadProfilePicture(MultipartFile file) {
        User user = getCurrentUser();

        try {

            Path uploadPath = Paths.get(uploadDir);

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            if (user.getProfilePictureUrl() != null && !user.getProfilePictureUrl().isEmpty()) {
                Path oldFile = Paths.get(user.getProfilePictureUrl());

                if (Files.exists(oldFile)) {
                    Files.delete(oldFile);
                }
            }

            //Generate unique file name

            String originalFileName = file.getOriginalFilename();

            String fileExtension = "";

            if (originalFileName != null && originalFileName.contains(".")) {
                fileExtension = originalFileName.substring(originalFileName.lastIndexOf("."));
            }

            String newFileName = UUID.randomUUID() + fileExtension;
            Path filePath = uploadPath.resolve(newFileName);

            Files.copy(file.getInputStream(), filePath);

            String fileUrl = uploadDir + newFileName;

            user.setProfilePictureUrl(fileUrl);
            userRepository.save(user);

            return Response.builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("Profile picture uploaded successfully")
                    .data(fileUrl)
                    .build();
        } catch (IOException ex) {
            throw new RuntimeException(ex.getMessage());

        }
    }

    @Override
    public Response<?> uploadProfilePictureToS3(MultipartFile file) {
        return null;
    }
}
