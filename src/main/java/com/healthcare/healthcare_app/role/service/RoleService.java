package com.healthcare.healthcare_app.role.service;

import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.role.entity.Role;

import java.util.List;

public interface RoleService {

    Response<Role> createRole(Role roleRequest);

    Response<Role> updateRole(Role roleRequest);

    Response<List<Role>> getAllRoles();

    Response<?> deleteRole(Long id);
}
