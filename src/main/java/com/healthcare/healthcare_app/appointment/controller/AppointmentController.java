package com.healthcare.healthcare_app.appointment.controller;

import com.healthcare.healthcare_app.appointment.dto.AppointmentDTO;
import com.healthcare.healthcare_app.appointment.service.AppointmentService;
import com.healthcare.healthcare_app.response.Response;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    public ResponseEntity<Response<AppointmentDTO>> bookAnAppointment(@RequestBody @Valid AppointmentDTO appointmentDTO){

        return new ResponseEntity<>(appointmentService.bookAnAppointment(appointmentDTO), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Response<List<AppointmentDTO>>> getMyAppointments(){

        return new ResponseEntity<>(appointmentService.getMyAppointments(),HttpStatus.OK);
    }

    @PostMapping("/cancel/{appointmentId}")
    public ResponseEntity<Response<AppointmentDTO>> cancelAppointment(@PathVariable("appointmentId") Long appointmentId){

        return new ResponseEntity<>(appointmentService.cancelAppointment(appointmentId), HttpStatus.OK);
    }

    @PutMapping("/complete/{appointmentId}")
    @PreAuthorize("hasAuthority('DOCTOR')")
    public ResponseEntity<Response<?>> completeAppointment(@PathVariable("appointmentId") Long appointmentId){

        return new ResponseEntity<>(appointmentService.completeAppointment(appointmentId), HttpStatus.OK);
    }
}
