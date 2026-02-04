package com.healthcare.healthcare_app.users.service;

import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.users.dto.UpdatePasswordRequest;
import com.healthcare.healthcare_app.users.dto.UserDTO;
import com.healthcare.healthcare_app.users.entity.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface UserService {

    User getCurrentUser();

    Response<UserDTO> getMyUserDetails();

    Response<UserDTO> getUserById(Long id);

    Response<List<UserDTO>> getAllUsers();

    Response<?> updatePassword(UpdatePasswordRequest updatePasswordRequest);
    Response<?> uploadProfilePicture(MultipartFile file);
    Response<?> uploadProfilePictureToS3(MultipartFile file);




}
