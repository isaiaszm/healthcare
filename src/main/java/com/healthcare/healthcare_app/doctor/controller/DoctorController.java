package com.healthcare.healthcare_app.doctor.controller;

import com.healthcare.healthcare_app.doctor.dto.DoctorDTO;
import com.healthcare.healthcare_app.doctor.service.DoctorService;
import com.healthcare.healthcare_app.enums.Specialization;
import com.healthcare.healthcare_app.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<DoctorDTO>> getDoctorProfile(){

        return new ResponseEntity<>(doctorService.getDoctorProfile(), HttpStatus.OK);
    }

    @PutMapping("/me")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<?>> updateDoctorProfile(@RequestBody DoctorDTO doctorDTO) {

        return new ResponseEntity<>(doctorService.updateDoctorProfile(doctorDTO), HttpStatus.OK);
    }
    @GetMapping
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<List<DoctorDTO>>> getAllDoctors(){

        return new ResponseEntity<>(doctorService.getAllDoctors(), HttpStatus.OK);
    }
    @GetMapping("/{doctorId}")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<DoctorDTO>> getDoctorById(@PathVariable("doctorId") Long doctorId){

        return new ResponseEntity<>(doctorService.getDoctorById(doctorId), HttpStatus.OK);
    }
    @GetMapping("/filter")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<List<DoctorDTO>>> searchDoctorsBySpecialization(@RequestParam Specialization specialization){

        return new ResponseEntity<>(doctorService.searchDoctorsBySpecialization(specialization), HttpStatus.OK);
    }
    @GetMapping("/specializations")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<List<Specialization>>> getAllSpecializations(){

        return new ResponseEntity<>(doctorService.getAllSpecializationEnums(), HttpStatus.OK);
    }
}
