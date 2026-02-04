package com.healthcare.healthcare_app.doctor.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.healthcare.healthcare_app.enums.Specialization;
import com.healthcare.healthcare_app.users.dto.UserDTO;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorDTO {


    private Long id;

    private String firstName;
    private String lastName;

    private Specialization specialization;

    private String licenseNumber;

    private UserDTO user;
}
