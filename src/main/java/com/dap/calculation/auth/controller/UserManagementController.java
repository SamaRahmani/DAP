
package com.dap.calculation.auth.controller;

import com.dap.calculation.auth.dto.UserResponse;
import com.dap.calculation.auth.dto.ChangeUserRoleRequest;
import com.dap.calculation.auth.dto.ChangeUserStatusRequest;
import com.dap.calculation.auth.service.UserManagementService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/users")
public class UserManagementController {

    private final UserManagementService userManagementService;

    public UserManagementController(
            UserManagementService userManagementService) {

        this.userManagementService = userManagementService;
    }

    @GetMapping
    public ResponseEntity<List<UserResponse>> getUsers(
            @RequestParam(required = false) String search) {

        List<UserResponse> users;

        if (search == null || search.isBlank()) {
            users = userManagementService.getAllUsers();
        } else {
            users = userManagementService.searchUsers(search);
        }

        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserResponse> getUserById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                userManagementService.getUserById(id)
        );
    }

    @PatchMapping("/{id}/role")
    public ResponseEntity<UserResponse> changeUserRole(
            @PathVariable Long id,
            @Valid @RequestBody ChangeUserRoleRequest request) {

        return ResponseEntity.ok(
                userManagementService.changeUserRole(
                        id,
                        request.getRole()
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<UserResponse> changeUserStatus(
            @PathVariable Long id,
            @Valid @RequestBody ChangeUserStatusRequest request) {

        return ResponseEntity.ok(
                userManagementService.changeUserStatus(
                        id,
                        request.getEnabled()
                )
        );
    }
}
