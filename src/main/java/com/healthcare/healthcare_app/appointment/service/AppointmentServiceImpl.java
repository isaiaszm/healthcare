package com.healthcare.healthcare_app.appointment.service;

import com.healthcare.healthcare_app.appointment.dto.AppointmentDTO;
import com.healthcare.healthcare_app.appointment.entity.Appointment;
import com.healthcare.healthcare_app.appointment.repository.AppointmentRepository;
import com.healthcare.healthcare_app.doctor.entity.Doctor;
import com.healthcare.healthcare_app.doctor.repository.DoctorRepository;
import com.healthcare.healthcare_app.enums.AppointmentStatus;
import com.healthcare.healthcare_app.exceptions.BadRequestException;
import com.healthcare.healthcare_app.exceptions.NotFoundException;
import com.healthcare.healthcare_app.notification.dto.NotificationDTO;
import com.healthcare.healthcare_app.notification.service.NotificationService;
import com.healthcare.healthcare_app.patient.entity.Patient;
import com.healthcare.healthcare_app.patient.repository.PatientRepository;
import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.users.entity.User;
import com.healthcare.healthcare_app.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AppointmentServiceImpl implements AppointmentService{

    private final AppointmentRepository appointmentRepository;
    private final DoctorRepository doctorRepository;
    private final PatientRepository patientRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;
    private final NotificationService notificationService;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' hh:mm a");
    @Override
    public Response<AppointmentDTO> bookAnAppointment(AppointmentDTO appointmentDTO) {

        User currentUser = userService.getCurrentUser();

        //Get patient initiating the booking
        Patient patient = patientRepository.findByUser(currentUser).orElseThrow(()-> new NotFoundException("Patient not found fot booking"));
        //Get the target doctor
        Doctor doctor = doctorRepository.findById(appointmentDTO.getDoctorId()).orElseThrow(()-> new NotFoundException("Doctor not found fot booking"));

        //Time given for the meeting
        LocalDateTime startTime = appointmentDTO.getStartTime();
        LocalDateTime endTime = startTime.plusMinutes(60);

        //Booking must be 1 hour in advance
        if (startTime.isBefore(LocalDateTime.now().plusHours(1))){
            throw new BadRequestException("Appointment must be booked 1 hour in advance.");
        }

        LocalDateTime checkStart = startTime.minusMinutes(60);

        List<Appointment> conflicts = appointmentRepository.findConflictingAppointments(
                doctor.getId(),
                checkStart,
                endTime
        );

        if (!conflicts.isEmpty()){
            throw new BadRequestException("Doctor is not available at the requested time. Please check their schedule.");
        }

        //Generating meeting link

        String uuid = UUID.randomUUID().toString().replace("-","");
        String uniqueRoomName = "dat-" + uuid.substring(0,10);

        //Use the public Jitsi Meet domain with your unique room name
        String meetingLink = "https://meet.jit.si/"+uniqueRoomName;

        log.info("Generated Jitsi meeting link: {}",meetingLink);

        //Build and save appointment
        Appointment appointment = Appointment.builder()
                .startTime(appointmentDTO.getStartTime())
                .endTime(endTime)
                .meetingLink(meetingLink)
                .initialSymptoms(appointmentDTO.getInitialSymptoms())
                .purposeOfConsultation(appointmentDTO.getPurposeOfConsultation())
                .status(AppointmentStatus.SCHEDULED)
                .doctor(doctor)
                .patient(patient)
                .build();

        Appointment savedAppointment = appointmentRepository.save(appointment);

        sendAppointmentConfirmation(savedAppointment);

        return Response.<AppointmentDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment booked successfully")
                .build();
    }

    private void sendAppointmentConfirmation(Appointment appointment) {
        // --- 1. Prepare Patient Notification ---
        User patientUser = appointment.getPatient().getUser();
        String formattedTime = appointment.getStartTime().format(FORMATTER);


        Map<String, Object> patientVars = new HashMap<>();
        patientVars.put("patientName", patientUser.getName());
        patientVars.put("doctorName", appointment.getDoctor().getUser().getName());
        patientVars.put("appointmentTime", formattedTime);
        patientVars.put("isVirtual", true);
        patientVars.put("meetingLink", appointment.getMeetingLink());
        patientVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());

        NotificationDTO patientNotification = NotificationDTO.builder()
                .recipient(patientUser.getEmail())
                .subject("DAT Health: Your Appointment is Confirmed")
                .templateName("patient-appointment")
                .templateVariables(patientVars)
                .build();


        // Dispatch patient email using the low-level service
        notificationService.sendEmail(patientNotification, patientUser);
        log.info("Dispatched confirmation email for patient: {}", patientUser.getEmail());


        // --- 2. Prepare Doctor Notification ---
        User doctorUser = appointment.getDoctor().getUser();

        Map<String, Object> doctorVars = new HashMap<>();
        doctorVars.put("doctorName", doctorUser.getName());
        doctorVars.put("patientFullName", patientUser.getName());
        doctorVars.put("appointmentTime", formattedTime);
        doctorVars.put("isVirtual", true);
        doctorVars.put("meetingLink", appointment.getMeetingLink());
        doctorVars.put("initialSymptoms", appointment.getInitialSymptoms());
        doctorVars.put("purposeOfConsultation", appointment.getPurposeOfConsultation());

        NotificationDTO doctorNotification = NotificationDTO.builder()
                .recipient(doctorUser.getEmail())
                .subject("DAT Health: New Appointment Booked")
                .templateName("doctor-appointment")
                .templateVariables(doctorVars)
                .build();


        // Dispatch doctor email using the low-level service
        notificationService.sendEmail(doctorNotification, doctorUser);
        log.info("Dispatched new appointment email for doctor: {}", doctorUser.getEmail());
    }

    @Override
    public Response<List<AppointmentDTO>> getMyAppointments() {
        User user = userService.getCurrentUser();

        Long userId  = user.getId();

        List<Appointment> appointments;

        //Check for doctor role
        boolean isDoctor = user.getRoles().stream().anyMatch(role -> role.getName().equals("DOCTOR"));

        if (isDoctor){
            doctorRepository.findByUser(user)
                    .orElseThrow(()-> new NotFoundException("Doctor profile not found"));

            //fetch appointments of the doctor

            appointments = appointmentRepository.findByDoctor_User_IdOrderByIdDesc(userId);
        }else{
            patientRepository.findByUser(user)
                    .orElseThrow(()-> new NotFoundException("Patient profile not found"));

            //fetch patient appointments
            appointments = appointmentRepository.findByPatient_User_IdOrderByIdDesc(userId);

        }

        List<AppointmentDTO> appointmentDTOS = appointments.stream()
                .map(appointment -> modelMapper.map(appointment,AppointmentDTO.class))
                .toList();


        return Response.<List<AppointmentDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointments retrieved successfully")
                .data(appointmentDTOS)
                .build();
    }

    @Override
    public Response<AppointmentDTO> cancelAppointment(Long appointmentId) {
        User user = userService.getCurrentUser();

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(()->new NotFoundException("Appointment not found"));

        //only the patient or doctor can cancel

        boolean isOwner = appointment.getPatient().getUser().getId().equals(user.getId()) ||
                appointment.getDoctor().getUser().getId().equals(user.getId());

        if (!isOwner){
            throw new BadRequestException("You do not have permission to cancel this meeting");
        }
        appointment.setStatus(AppointmentStatus.CANCELLED);
        Appointment saveAppointment = appointmentRepository.save(appointment);

        sendAppointmentCancellation(saveAppointment,user);

        return Response.<AppointmentDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment cancelled successfully")
                .build();
    }

    private void sendAppointmentCancellation(Appointment appointment, User cancelingUser) {

        User patientUser = appointment.getPatient().getUser();
        User doctorUser = appointment.getDoctor().getUser();

        // Safety check to ensure the cancellingUser is involved
        boolean isOwner = patientUser.getId().equals(cancelingUser.getId()) || doctorUser.getId().equals(cancelingUser.getId());
        if (!isOwner) {
            log.error("Cancellation initiated by user not associated with appointment. User ID: {}", cancelingUser.getId());
            return;
        }

        String formattedTime = appointment.getStartTime().format(FORMATTER);
        String cancellingPartyName = cancelingUser.getName();


        // --- Common Variables for the Template ---
        Map<String, Object> baseVars = new HashMap<>();
        baseVars.put("cancellingPartyName", cancellingPartyName);
        baseVars.put("appointmentTime", formattedTime);
        baseVars.put("doctorName", appointment.getDoctor().getLastName());
        baseVars.put("patientFullName", patientUser.getName());

        // --- 1. Dispatch Email to Doctor ---
        Map<String, Object> doctorVars = new HashMap<>(baseVars);
        doctorVars.put("recipientName", doctorUser.getName());

        NotificationDTO doctorNotification = NotificationDTO.builder()
                .recipient(doctorUser.getEmail())
                .subject("DAT Health: Appointment Cancellation")
                .templateName("appointment-cancellation")
                .templateVariables(doctorVars)
                .build();

        notificationService.sendEmail(doctorNotification, doctorUser);
        log.info("Dispatched cancellation email to Doctor: {}", doctorUser.getEmail());


        // --- 2. Dispatch Email to Patient ---
        Map<String, Object> patientVars = new HashMap<>(baseVars);
        patientVars.put("recipientName", patientUser.getName());

        NotificationDTO patientNotification = NotificationDTO.builder()
                .recipient(patientUser.getEmail())
                .subject("DAT Health: Appointment CANCELED (ID: " + appointment.getId() + ")")
                .templateName("appointment-cancellation")
                .templateVariables(patientVars)
                .build();

        notificationService.sendEmail(patientNotification, patientUser);
        log.info("Dispatched cancellation email to Patient: {}", patientUser.getEmail());
    }

    @Override
    public Response<?> completeAppointment(Long appointmentId) {
        User user = userService.getCurrentUser();

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(()->new NotFoundException("Appointment not found wit ID: "+appointmentId));

        if (!appointment.getDoctor().getUser().getId().equals(user.getId())){
            throw new BadRequestException("Only the assigned doctor can mark this appointment as complete.");
        }

        //update status and time
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointment.setEndTime(LocalDateTime.now());

        appointmentRepository.save(appointment);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Appointment successfully marked as completed. You may now proceed to create the consultation notes")
                .build();

    }
}
