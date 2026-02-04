package com.healthcare.healthcare_app.users.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResetPasswordRequest {
    //will be used to request for forgot password
    private String email;

    //will be used to set new password
    private String code;
    private String newPassword;
}
