package com.healthcare.healthcare_app.consultation.service;

import com.healthcare.healthcare_app.appointment.entity.Appointment;
import com.healthcare.healthcare_app.appointment.repository.AppointmentRepository;
import com.healthcare.healthcare_app.consultation.dto.ConsultationDTO;
import com.healthcare.healthcare_app.consultation.entity.Consultation;
import com.healthcare.healthcare_app.consultation.repository.ConsultationRepository;
import com.healthcare.healthcare_app.doctor.repository.DoctorRepository;
import com.healthcare.healthcare_app.enums.AppointmentStatus;
import com.healthcare.healthcare_app.exceptions.BadRequestException;
import com.healthcare.healthcare_app.exceptions.NotFoundException;
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
import java.util.List;


@Service
@Slf4j
@RequiredArgsConstructor
public class ConsultationServiceImpl implements ConsultationService{

    private final ConsultationRepository consultationRepository;
    private final PatientRepository patientRepository;
    private final AppointmentRepository appointmentRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response<ConsultationDTO> createConsultation(ConsultationDTO consultationDTO) {

        User user = userService.getCurrentUser();

        Long appointmentId = consultationDTO.getAppointmentId();

        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(()-> new NotFoundException("Appointment not found"));

        //check for the doctor linked to the appointment
        if (!appointment.getDoctor().getUser().getId().equals(user.getId())){
            throw new BadRequestException("You are not authorized to create notes for this consultation.");
        }

        //complete the appointment
        appointment.setStatus(AppointmentStatus.COMPLETED);
        appointmentRepository.save(appointment);

        if (consultationRepository.findByAppointmentId(appointmentId).isPresent()){
            throw new BadRequestException("Consultation notes already exists for this appointment.");
        }

        Consultation consultation = Consultation.builder()
                .consultationDate(LocalDateTime.now())
                .subjectiveNotes(consultationDTO.getSubjectiveNotes())
                .objectiveFindings(consultationDTO.getObjectiveFindings())
                .assessment(consultationDTO.getAssessment())
                .plan(consultationDTO.getPlan())
                .appointment(appointment)
                .build();

        consultationRepository.save(consultation);

        ConsultationDTO consulDTO = modelMapper.map(consultation,ConsultationDTO.class);

        return Response.<ConsultationDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation notes saved successfully")
                .data(consulDTO)
                .build();
    }

    @Override
    public Response<ConsultationDTO> getConsultationByAppointmentId(Long appointmentId) {
        Consultation consultation = consultationRepository.findByAppointmentId(appointmentId)
                .orElseThrow(()-> new NotFoundException("Consultation not found"));

        ConsultationDTO consulDTO = modelMapper.map(consultation,ConsultationDTO.class);

        return Response.<ConsultationDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation retrieved successfully")
                .data(consulDTO)
                .build();
    }

    @Override
    public Response<List<ConsultationDTO>> getConsultationHistoryFotPatient(Long patientId) {

        User user = userService.getCurrentUser();

        //retrieved patienId if param patientId is null
        if (patientId == null){

            Patient currentPatient = patientRepository.findByUser(user)
                    .orElseThrow(()-> new NotFoundException("Patient profile not found for the current user."));

            patientId = currentPatient.getId();
        }

        patientRepository.findById(patientId)
                .orElseThrow(()-> new NotFoundException("Patient not found"));

        List<Consultation> history = consultationRepository.findByAppointmentPatientIdOrderByConsultationDateDesc(patientId);

        if (history.isEmpty()){
            return Response.<List<ConsultationDTO>>builder()
                    .statusCode(HttpStatus.OK.value())
                    .message("No consultation history for this patient")
                    .data(List.of())
                    .build();
        }

        List<ConsultationDTO> historyDTOS = history.stream()
                .map(consultation -> modelMapper.map(consultation,ConsultationDTO.class))
                .toList();

        return Response.<List<ConsultationDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Consultation history retrieved successfully")
                .data(historyDTOS)
                .build();


    }
}
