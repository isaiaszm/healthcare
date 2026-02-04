package com.healthcare.healthcare_app.patient.service;

import com.healthcare.healthcare_app.enums.BloodGroup;
import com.healthcare.healthcare_app.enums.Genotype;
import com.healthcare.healthcare_app.patient.dto.PatientDTO;
import com.healthcare.healthcare_app.response.Response;

import java.util.List;

public interface PatientService {

    Response<PatientDTO> getPatientProfile();
    Response<?> updatePatientProfile(PatientDTO patientDTO);
    Response<PatientDTO> getPatientById(Long patientId);

    Response<List<BloodGroup>> getAllBloodGroupsEnums();
    Response<List<Genotype>> getAllGenotypeEnums();


}
