package com.healthcare.healthcare_app.consultation.service;

import com.healthcare.healthcare_app.consultation.dto.ConsultationDTO;
import com.healthcare.healthcare_app.response.Response;

import java.util.List;

public interface ConsultationService {

    Response<ConsultationDTO> createConsultation(ConsultationDTO consultationDTO);

    Response<ConsultationDTO> getConsultationByAppointmentId(Long appointmentId);

    Response<List<ConsultationDTO>> getConsultationHistoryFotPatient(Long patientId);

}
