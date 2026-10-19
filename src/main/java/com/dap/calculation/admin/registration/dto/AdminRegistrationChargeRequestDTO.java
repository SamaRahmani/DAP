package com.dap.calculation.admin.registration.dto;

import java.math.BigDecimal;

public class AdminRegistrationChargeRequestDTO {

    private String chargeScope;
    private String stateCode;
    private Long cityId;
    private String chargeName;
    private BigDecimal chargeValue;

    public AdminRegistrationChargeRequestDTO() {
    }

    public String getChargeScope() {
        return chargeScope;
    }

    public void setChargeScope(String chargeScope) {
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