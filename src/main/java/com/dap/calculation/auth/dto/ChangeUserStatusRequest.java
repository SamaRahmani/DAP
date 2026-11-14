
package com.dap.calculation.auth.dto;

import jakarta.validation.constraints.NotNull;

public class ChangeUserStatusRequest {

    @NotNull(message = "Enabled status is required")
    private Boolean enabled;

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
}
