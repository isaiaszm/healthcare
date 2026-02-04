package com.healthcare.healthcare_app.users.controller;

import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.users.dto.UpdatePasswordRequest;
import com.healthcare.healthcare_app.users.dto.UserDTO;
import com.healthcare.healthcare_app.users.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<Response<UserDTO>> getMyUserDetails(){

        return new ResponseEntity<>(userService.getMyUserDetails(), HttpStatus.OK);
    }
    @GetMapping("/by-id/{userId}")
    public ResponseEntity<Response<UserDTO>> getUseById(@PathVariable("userId") Long id){

        return new ResponseEntity<>(userService.getUserById(id), HttpStatus.OK);
    }
    @GetMapping("/all")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<Response<List<UserDTO>>> getAllUsers(){

        return new ResponseEntity<>(userService.getAllUsers(), HttpStatus.OK);
    }

    @PutMapping("/update-password")
    public ResponseEntity<Response<?>> updatePassword(@RequestBody @Valid UpdatePasswordRequest updatePasswordRequest){

        return new ResponseEntity<>(userService.updatePassword(updatePasswordRequest), HttpStatus.OK);
    }
    @PutMapping("/profile-picture")
    public ResponseEntity<Response<?>> uploadProfilePicture(@RequestParam("file")MultipartFile file){

        return new ResponseEntity<>(userService.uploadProfilePicture(file), HttpStatus.OK);
    }
}
