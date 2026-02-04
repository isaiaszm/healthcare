package com.healthcare.healthcare_app.role.controller;

import com.healthcare.healthcare_app.response.Response;
import com.healthcare.healthcare_app.role.entity.Role;
import com.healthcare.healthcare_app.role.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN')")
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    @GetMapping
    public ResponseEntity<Response<List<Role>>> getAllRoles(){

        return new ResponseEntity<>(roleService.getAllRoles(),HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Response<Role>> createRole(@RequestBody Role roleRequest){

        return new ResponseEntity<>(roleService.createRole(roleRequest), HttpStatus.CREATED);
    }

    @PutMapping
    public ResponseEntity<Response<Role>> updateRole(@RequestBody Role requestRole){

        return new ResponseEntity<>(roleService.updateRole(requestRole),HttpStatus.OK);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<Response<?>> deleteRole(@PathVariable("roleId") Long id){

        return new ResponseEntity<>(roleService.deleteRole(id),HttpStatus.OK);
    }


}
