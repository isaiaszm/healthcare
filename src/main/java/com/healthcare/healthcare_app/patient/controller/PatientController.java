package com.healthcare.healthcare_app.patient.controller;

import com.healthcare.healthcare_app.enums.BloodGroup;
import com.healthcare.healthcare_app.enums.Genotype;
import com.healthcare.healthcare_app.patient.dto.PatientDTO;
import com.healthcare.healthcare_app.patient.service.PatientService;
import com.healthcare.healthcare_app.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/patients")
public class PatientController {

    private final PatientService patientService;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<PatientDTO>> getPatientProfile() {

        return new ResponseEntity<>(patientService.getPatientProfile(), HttpStatus.OK);
    }

    @PutMapping("/me")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<?>> updatePatientProfile(@RequestBody PatientDTO patientDTO) {

        return new ResponseEntity<>(patientService.updatePatientProfile(patientDTO), HttpStatus.OK);
    }

    @GetMapping("/{patientId}")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<PatientDTO>> getPatientById(@PathVariable("patientId") Long patientId) {

        return new ResponseEntity<>(patientService.getPatientById(patientId), HttpStatus.OK);
    }

    @GetMapping("/bloodgroup")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<List<BloodGroup>>> getAllBloodGroupsEnums() {

        return new ResponseEntity<>(patientService.getAllBloodGroupsEnums(), HttpStatus.OK);
    }

    @GetMapping("/genotype")
    @PreAuthorize("hasAuthority('PATIENT')")
    public ResponseEntity<Response<List<Genotype>>> getAllGenotypeEnums() {

        return new ResponseEntity<>(patientService.getAllGenotypeEnums(), HttpStatus.OK);
    }

}