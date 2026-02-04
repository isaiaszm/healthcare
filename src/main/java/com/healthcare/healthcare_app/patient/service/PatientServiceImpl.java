package com.healthcare.healthcare_app.patient.service;

import com.healthcare.healthcare_app.enums.BloodGroup;
import com.healthcare.healthcare_app.enums.Genotype;
import com.healthcare.healthcare_app.exceptions.NotFoundException;
import com.healthcare.healthcare_app.patient.dto.PatientDTO;
import com.healthcare.healthcare_app.patient.entity.Patient;
import com.healthcare.healthcare_app.patient.repository.PatientRepository;
import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.users.entity.User;
import com.healthcare.healthcare_app.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PatientServiceImpl implements PatientService{
    private final PatientRepository patientRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response<PatientDTO> getPatientProfile() {

        User user = userService.getCurrentUser();

        Patient patient = patientRepository.findByUser(user).orElseThrow(()->new NotFoundException("Patient not found"));

        PatientDTO patientDTO = modelMapper.map(patient, PatientDTO.class);

        return Response.<PatientDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient profile retrieved successfully")
                .data(patientDTO)
                .build();

    }

    @Override
    public Response<?> updatePatientProfile(PatientDTO patientDTO) {

        User user = userService.getCurrentUser();

       Patient patient = patientRepository.findByUser(user).orElseThrow(()->new NotFoundException("Patient not found"));

        updatePatient(patient,patientDTO);

       patientRepository.save(patient);


        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient profile updated successfully")
                .build();
    }

    private void updatePatient(Patient patient,PatientDTO patientDTO) {

        if (StringUtils.hasText(patientDTO.getFirstName())){
                patient.setFirstName(patientDTO.getFirstName());

        }
        if (StringUtils.hasText(patientDTO.getFirstName())){

            patient.setLastName(patientDTO.getLastName());
        }
        if (StringUtils.hasText(patientDTO.getFirstName())){
            patient.setPhone(patientDTO.getPhone());
        }

        Optional.ofNullable(patientDTO.getDateOfBirth()).ifPresent(patient::setDateOfBirth);

        if (StringUtils.hasText(patientDTO.getKnownAllergies())){
            patient.setKnownAllergies(patientDTO.getKnownAllergies());
        }

        Optional.ofNullable(patientDTO.getGenotype()).ifPresent(patient::setGenotype);
        Optional.ofNullable(patientDTO.getBloodGroup()).ifPresent(patient::setBloodGroup);
    }

    @Override
    public Response<PatientDTO> getPatientById(Long patientId) {

        Patient patient = patientRepository.findById(patientId).orElseThrow(()->new NotFoundException("Patient not found"));

        PatientDTO patientDTO = modelMapper.map(patient, PatientDTO.class);


        return Response.<PatientDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Patient retrieved by ID")
                .data(patientDTO)
                .build();
    }

    @Override
    public Response<List<BloodGroup>> getAllBloodGroupsEnums() {

        List<BloodGroup> bloodGroups = Arrays.asList(BloodGroup.values());


        return Response.<List<BloodGroup>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("BloodGroups retrieved successfully")
                .data(bloodGroups)
                .build();
    }

    @Override
    public Response<List<Genotype>> getAllGenotypeEnums() {
        List<Genotype> genotypes = Arrays.asList(Genotype.values());


        return Response.<List<Genotype>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Genotypes retrieved successfully")
                .data(genotypes)
                .build();
    }
}
