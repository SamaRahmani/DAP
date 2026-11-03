
package com.dap.calculation.admin.registration.dto;

import com.dap.calculation.enums.ChargeScope;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class AdminRegistrationChargeRequestDTO {

    @NotNull(message = "Charge scope is required")
    private ChargeScope chargeScope;

    @Size(max = 10, message = "State code cannot exceed 10 characters")
    private String stateCode;

    private Long cityId;

    @NotBlank(message = "Charge name is required")
    @Size(max = 100, message = "Charge name cannot exceed 100 characters")
    private String chargeName;

    @NotNull(message = "Charge value is required")
    @DecimalMin(value = "0.0", message = "Charge value cannot be negative")
    @Digits(
            integer = 11,
            fraction = 4,
            message = "Charge value must have at most 11 integer digits and 4 decimal places"
    )
    private BigDecimal chargeValue;

    public AdminRegistrationChargeRequestDTO() {
    }

    public ChargeScope getChargeScope() {
        return chargeScope;
    }

    public void setChargeScope(ChargeScope chargeScope) {
        this.chargeScope = chargeScope;
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public Long getCityId() {
        return cityId;
    }

    public void setCityId(Long cityId) {
        this.cityId = cityId;
    }

    public String getChargeName() {
        return chargeName;
    }

    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    public BigDecimal getChargeValue() {
        return chargeValue;
    }

    public void setChargeValue(BigDecimal chargeValue) {
        this.chargeValue = chargeValue;
    }
}
