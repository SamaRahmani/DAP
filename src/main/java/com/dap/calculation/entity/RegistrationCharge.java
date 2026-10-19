package com.dap.calculation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.math.BigDecimal;

@Entity
@Table(name = "registration_charge")
public class RegistrationCharge {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "registration_charge_id")
    private Long registrationChargeId;

    @Column(name = "charge_scope", nullable = false, length = 10)
    private String chargeScope;

    @Column(name = "state_code", nullable = false, length = 10)
    private String stateCode;

    @Column(name = "city_id")
    private Long cityId;

    @Column(name = "charge_name", nullable = false, length = 100)
    private String chargeName;

    @Column(name = "charge_value", nullable = false, precision = 15, scale = 4)
    private BigDecimal chargeValue;

    public RegistrationCharge() {
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