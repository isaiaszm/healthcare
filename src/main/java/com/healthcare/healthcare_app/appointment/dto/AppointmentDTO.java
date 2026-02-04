package com.healthcare.healthcare_app.appointment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare.healthcare_app.doctor.dto.DoctorDTO;
import com.healthcare.healthcare_app.enums.AppointmentStatus;
import com.healthcare.healthcare_app.patient.dto.PatientDTO;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class AppointmentDTO {

    private Long id;

    @NotNull(message = "Doctor is required for booking.")
    private Long doctorId;

    private String purposeOfConsultation;

    private String initialSymptoms;

    @NotNull(message = "Start time is required for the appointment.")
    @Future(message = "Appointment must be scheduled for a future date and time.")
    private LocalDateTime startTime;

    private LocalDateTime endTime;
    private String meetingLink;
    private AppointmentStatus status;

    private DoctorDTO doctor;
    private PatientDTO patient;
}