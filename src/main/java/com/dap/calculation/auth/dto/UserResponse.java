
package com.dap.calculation.auth.dto;

import com.dap.calculation.auth.entity.User;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.stream.Collectors;

public class UserResponse {

    private Long id;
    private String fullName;
    private String email;
    private boolean enabled;
    private LocalDateTime createdAt;
    private Set<String> roles;

    public UserResponse(User user) {
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.email = user.getEmail();
        this.enabled = user.isEnabled();
        this.createdAt = user.getCreatedAt();

        this.roles = user.getRoles()
                .stream()
                .map(role -> role.getName())
                .collect(Collectors.toSet());
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<String> getRoles() {
        return roles;
    }
}
