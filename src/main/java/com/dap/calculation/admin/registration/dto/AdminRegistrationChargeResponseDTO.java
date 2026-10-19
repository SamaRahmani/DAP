package com.dap.calculation.admin.registration.dto;

import java.math.BigDecimal;

public class AdminRegistrationChargeResponseDTO {

    private Long registrationChargeId;
    private String chargeScope;
    private String stateCode;
    private Long cityId;
    private String chargeName;
    private BigDecimal chargeValue;

    public AdminRegistrationChargeResponseDTO() {
    }

    public AdminRegistrationChargeResponseDTO(
            Long registrationChargeId,
            String chargeScope,
            String stateCode,
            Long cityId,
            String chargeName,
            BigDecimal chargeValue) {

        this.registrationChargeId = registrationChargeId;
        this.chargeScope = chargeScope;
        this.stateCode = stateCode;
        this.cityId = cityId;
        this.chargeName = chargeName;
        this.chargeValue = chargeValue;
    }

    public Long getRegistrationChargeId() {
        return registrationChargeId;
    }

    public void setRegistrationChargeId(Long registrationChargeId) {
        this.registrationChargeId = registrationChargeId;
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