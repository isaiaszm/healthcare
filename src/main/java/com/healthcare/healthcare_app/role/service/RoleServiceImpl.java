package com.healthcare.healthcare_app.role.service;

import com.healthcare.healthcare_app.exceptions.NotFoundException;
import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.role.entity.Role;
import com.healthcare.healthcare_app.role.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class RoleServiceImpl implements RoleService{
    private final RoleRepository roleRepository;

    @Override
    public Response<Role> createRole(Role roleRequest) {
        Role savedRole = roleRepository.save(roleRequest);
        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role saved successfully")
                .data(savedRole)
                .build();

    }

    @Override
    public Response<Role> updateRole(Role roleRequest) {
        Role foundRole = roleRepository.findById(roleRequest.getId())
                .orElseThrow(()-> new NotFoundException("Role not found"));

        foundRole.setName(roleRequest.getName());

        Role updatedRole = roleRepository.save(foundRole);

        return Response.<Role>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role updated successfully")
                .data(updatedRole)
                .build();
    }

    @Override
    public Response<List<Role>> getAllRoles() {
        List<Role> roles = roleRepository.findAll();
        return Response.<List<Role>>builder()
                .statusCode(HttpStatus.OK.value())
                .message("Roles retrieved successfully")
                .data(roles)
                .build();
    }

    @Override
    public Response<?> deleteRole(Long id) {
        Role foundRole = roleRepository.findById(id)
                .orElseThrow(()-> new NotFoundException("Role not found"));

        roleRepository.delete(foundRole);

        return Response.builder()
                .statusCode(HttpStatus.OK.value())
                .message("Role deleted successfully")
                .build();
    }
}
