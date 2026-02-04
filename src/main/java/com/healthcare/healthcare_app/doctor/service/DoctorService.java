package com.healthcare.healthcare_app.doctor.service;

import com.healthcare.healthcare_app.doctor.dto.DoctorDTO;
import com.healthcare.healthcare_app.enums.Specialization;
import com.healthcare.healthcare_app.response.Response;

import java.util.List;

public interface DoctorService {

    Response<DoctorDTO> getDoctorProfile();
    Response<?> updateDoctorProfile(DoctorDTO doctorDTO);
    Response<List<DoctorDTO>> getAllDoctors();
    Response<DoctorDTO> getDoctorById(Long doctorId);

    Response<List<DoctorDTO>> searchDoctorsBySpecialization(Specialization specialization);
    Response<List<Specialization>> getAllSpecializationEnums();


}
