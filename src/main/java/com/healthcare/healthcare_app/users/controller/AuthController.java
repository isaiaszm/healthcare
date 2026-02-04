package com.healthcare.healthcare_app.users.controller;

import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.users.dto.LoginRequest;
import com.healthcare.healthcare_app.users.dto.LoginResponse;
import com.healthcare.healthcare_app.users.dto.RegistrationRequest;
import com.healthcare.healthcare_app.users.dto.ResetPasswordRequest;
import com.healthcare.healthcare_app.users.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<Response<String>> register(@RequestBody @Valid RegistrationRequest registrationRequest){

        return new ResponseEntity<>(authService.register(registrationRequest), HttpStatus.CREATED);

    }
    @PostMapping("/login")
    public ResponseEntity<Response<LoginResponse>> login(@RequestBody @Valid LoginRequest loginRequest){

        return new ResponseEntity<>(authService.login(loginRequest), HttpStatus.OK);

    }
    @PostMapping("/forgot-password")
    public ResponseEntity<Response<?>> forgotPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){

        return new ResponseEntity<>(authService.forgetPassword(resetPasswordRequest.getEmail()), HttpStatus.OK);

    }
    @PostMapping("/reset-password")
    public ResponseEntity<Response<?>> resetPassword(@RequestBody ResetPasswordRequest resetPasswordRequest){

        return new ResponseEntity<>(authService.updatePasswordViaResetCode(resetPasswordRequest), HttpStatus.OK);

    }
}
