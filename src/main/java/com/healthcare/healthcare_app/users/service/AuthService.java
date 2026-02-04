package com.healthcare.healthcare_app.users.service;

import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.users.dto.LoginRequest;
import com.healthcare.healthcare_app.users.dto.LoginResponse;
import com.healthcare.healthcare_app.users.dto.RegistrationRequest;
import com.healthcare.healthcare_app.users.dto.ResetPasswordRequest;

public interface AuthService {
    Response<String> register(RegistrationRequest registrationRequest);
    Response<LoginResponse> login(LoginRequest loginRequest);

    Response<?> forgetPassword(String email);
    Response<?> updatePasswordViaResetCode(ResetPasswordRequest resetPasswordRequest);
}
