package com.healthcare.healthcare_app.consultation.entity;

import com.healthcare.healthcare_app.appointment.entity.Appointment;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "consultations")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime consultationDate;

    @Lob
    private String subjectiveNotes;

    @Lob
    private String objectiveFindings;

    @Lob
    private String assessment;

    @Lob
    private String plan;

    @OneToOne
    @JoinColumn(name = "appointment_id", unique = true, nullable = false)
    private Appointment appointment;
}
