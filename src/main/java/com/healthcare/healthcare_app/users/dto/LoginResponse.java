package com.healthcare.healthcare_app.users.dto;

import com.healthcare.healthcare_app.role.entity.Role;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Setter
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {
    private String token;
    private Set<String> roles = new HashSet<>();
}
