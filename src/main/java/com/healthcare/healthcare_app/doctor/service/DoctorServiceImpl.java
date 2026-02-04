package com.healthcare.healthcare_app.doctor.service;

import com.healthcare.healthcare_app.doctor.dto.DoctorDTO;
import com.healthcare.healthcare_app.doctor.entity.Doctor;
import com.healthcare.healthcare_app.doctor.repository.DoctorRepository;
import com.healthcare.healthcare_app.enums.Specialization;
import com.healthcare.healthcare_app.exceptions.NotFoundException;
import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.users.entity.User;
import com.healthcare.healthcare_app.users.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class DoctorServiceImpl implements DoctorService{

    private final DoctorRepository doctorRepository;
    private final UserService userService;
    private final ModelMapper modelMapper;

    @Override
    public Response<DoctorDTO> getDoctorProfile() {
        User user = userService.getCurrentUser();

        Doctor doctor = doctorRepository.findByUser(user)
                .orElseThrow(()-> new NotFoundException("Doctor not found"));

        DoctorDTO doctorDTO = modelMapper.map(doctor,DoctorDTO.class);

        return Response.<DoctorDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor profile retrieved successfully")
                .data(doctorDTO)
                .build();
    }

    @Override
    public Response<?> updateDoctorProfile(DoctorDTO doctorDTO) {
        User user = userService.getCurrentUser();

        Doctor doctor = doctorRepository.findByUser(user).orElseThrow(()-> new NotFoundException("Doctor not found"));

        updateDoctor(doctor,doctorDTO);

        doctorRepository.save(doctor);
        log.info("Doctor profile updated");

        DoctorDTO dto = modelMapper.map(doctor,DoctorDTO.class);


        return Response.<DoctorDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor profile updated successfully")
                .data(dto)
                .build();
    }

    private void updateDoctor(Doctor doctor, DoctorDTO doctorDTO) {

        if (StringUtils.hasText(doctorDTO.getFirstName())){
            doctor.setFirstName(doctorDTO.getFirstName());
        }
        if (StringUtils.hasText(doctorDTO.getLastName())){
            doctor.setLastName(doctorDTO.getLastName());
        }
        if (StringUtils.hasText(doctorDTO.getLicenseNumber())){
            doctor.setLicenseNumber(doctorDTO.getLicenseNumber());
        }

        Optional.ofNullable(doctorDTO.getSpecialization()).ifPresent(doctor::setSpecialization);
    }

    @Override
    public Response<List<DoctorDTO>> getAllDoctors() {
        List<DoctorDTO> doctorDTOList = doctorRepository.findAll().stream()
                .map(doctor -> modelMapper.map(doctor,DoctorDTO.class))
                .toList();
        log.info("fetching all doctors");


        return Response.<List<DoctorDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Fetching doctors successfully")
                .data(doctorDTOList)
                .build();
    }

    @Override
    public Response<DoctorDTO> getDoctorById(Long doctorId) {

        Doctor doctor = doctorRepository.findById(doctorId)
                .orElseThrow(()->new NotFoundException("Doctor not found"));

        DoctorDTO doctorDTO = modelMapper.map(doctor,DoctorDTO.class);


        return Response.<DoctorDTO>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Doctor retrieved successfully")
                .data(doctorDTO)
                .build();
    }

    @Override
    public Response<List<DoctorDTO>> searchDoctorsBySpecialization(Specialization specialization) {
        List<DoctorDTO> doctorDTOList = doctorRepository.findBySpecialization(specialization).stream()
                .map(doctor -> modelMapper.map(doctor,DoctorDTO.class))
                .toList();



        String message = doctorDTOList.isEmpty() ?
                "No doctors found for specialization: "+specialization.name():
                "Doctors retrieved successfully for specialization: "+specialization.name();

        return Response.<List<DoctorDTO>>builder()
                .statusCode(HttpStatus.OK.value())
                .message(message)
                .data(doctorDTOList)
                .build();
    }

    @Override
    public Response<List<Specialization>> getAllSpecializationEnums() {
        List<Specialization> specializationList = Arrays.asList(Specialization.values());

        return Response.<List<Specialization>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Specializations retrieved successfully")
                .data(specializationList)
                .build();
    }
}
