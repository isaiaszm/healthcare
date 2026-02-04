package com.healthcare.healthcare_app.consultation.controller;

import com.healthcare.healthcare_app.consultation.dto.ConsultationDTO;
import com.healthcare.healthcare_app.consultation.service.ConsultationService;
import com.healthcare.healthcare_app.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consultations")
public class ConsultationController {

    private final ConsultationService consultationService;

    @PostMapping
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<ConsultationDTO>> createConsultation(@RequestBody ConsultationDTO consultationDTO){

        return new ResponseEntity<>(consultationService.createConsultation(consultationDTO), HttpStatus.CREATED);
    }

    @GetMapping("/appointment/{appointmentId}")
    public ResponseEntity<Response<ConsultationDTO>> getConsultationByAppointmentId(@PathVariable("appointmentId") Long appointmentId){

        return new ResponseEntity<>(consultationService.getConsultationByAppointmentId(appointmentId),HttpStatus.OK);
    }


    @GetMapping("/history")
    public ResponseEntity<Response<List<ConsultationDTO>>> getConsultationHistoryFotPatient(@RequestParam(required = false) Long patientId){

        return new ResponseEntity<>(consultationService.getConsultationHistoryFotPatient(patientId),HttpStatus.OK);
    }
}
