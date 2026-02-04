package com.healthcare.healthcare_app.appointment.service;

import com.healthcare.healthcare_app.appointment.dto.AppointmentDTO;
import com.healthcare.healthcare_app.response.Response;

import java.util.List;

public interface AppointmentService {

    Response<AppointmentDTO> bookAnAppointment(AppointmentDTO appointmentDTO);

    Response<List<AppointmentDTO>> getMyAppointments();

    Response<AppointmentDTO> cancelAppointment(Long appointmentId);

    Response<?> completeAppointment(Long appointmentId);
}
