
package com.dap.calculation.auth.service;

import com.dap.calculation.auth.dto.UserResponse;
import com.dap.calculation.auth.entity.Role;
import com.dap.calculation.auth.entity.User;
import com.dap.calculation.auth.repository.RoleRepository;
import com.dap.calculation.auth.repository.UserRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class UserManagementService {

    private static final String ROLE_USER = "ROLE_USER";
    private static final String ROLE_ADMIN = "ROLE_ADMIN";

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;

    public UserManagementService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {

        return userRepository.findAll()
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<UserResponse> searchUsers(String search) {

        return userRepository
                .findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                        search,
                        search
                )
                .stream()
                .map(UserResponse::new)
                .toList();
    }

    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {

        User user = findUser(id);

        return new UserResponse(user);
    }

    @Transactional
    public UserResponse changeUserRole(
            Long userId,
            String requestedRole) {

        // Lock the admin role row to serialize administrator changes.
        Role adminRole = roleRepository.findByNameForUpdate(ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_ADMIN not found"
                ));

        String roleName = requestedRole.trim().toUpperCase();

        if (!ROLE_ADMIN.equals(roleName)
                && !ROLE_USER.equals(roleName)) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Only ROLE_USER and ROLE_ADMIN are supported"
            );
        }

        User user = findUser(userId);

        boolean currentlyAdmin = user.getRoles()
                .stream()
                .anyMatch(role -> ROLE_ADMIN.equals(role.getName()));

        boolean changingToAdmin = ROLE_ADMIN.equals(roleName);

        // Prevent removal of the last enabled administrator.
        if (currentlyAdmin && !changingToAdmin && user.isEnabled()) {

            long adminCount =
                    userRepository.countEnabledUsersByRoleName(ROLE_ADMIN);

            if (adminCount <= 1) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Cannot demote the last enabled administrator"
                );
            }
        }

        Role targetRole = changingToAdmin
                ? adminRole
                : roleRepository.findByName(ROLE_USER)
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_USER not found"
                ));

        // This operation intentionally makes the roles mutually exclusive.
        user.setRoles(new HashSet<>(Set.of(targetRole)));

        userRepository.saveAndFlush(user);

        // Invalidate old JWTs and refresh tokens.
        refreshTokenService.revokeAllUserSessions(userId);

        return new UserResponse(user);
    }

    @Transactional
    public UserResponse changeUserStatus(
            Long userId,
            Boolean enabled) {

        // Serialize status changes involving administrators too.
        roleRepository.findByNameForUpdate(ROLE_ADMIN)
                .orElseThrow(() -> new IllegalStateException(
                        "ROLE_ADMIN not found"
                ));

        User user = findUser(userId);

        if (enabled == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Enabled status is required"
            );
        }

        boolean currentlyAdmin = user.getRoles()
                .stream()
                .anyMatch(role -> ROLE_ADMIN.equals(role.getName()));

        // Prevent disabling the last enabled administrator.
        if (!enabled && user.isEnabled() && currentlyAdmin) {

            long adminCount =
                    userRepository.countEnabledUsersByRoleName(ROLE_ADMIN);

            if (adminCount <= 1) {
                throw new ResponseStatusException(
                        HttpStatus.CONFLICT,
                        "Cannot disable the last enabled administrator"
                );
            }
        }

        if (user.isEnabled() == enabled) {
            return new UserResponse(user);
        }

        user.setEnabled(enabled);

        userRepository.saveAndFlush(user);

        // Revoke sessions when account status changes.
        refreshTokenService.revokeAllUserSessions(userId);

        return new UserResponse(user);
    }

    private User findUser(Long userId) {

        return userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "User not found"
                ));
    }
}
