package com.dap.calculation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "rto")
public class Rto {

    @Id
    @Column(name = "rto_code", length = 20)
    private String rtoCode;

    @Column(name = "rto_name", nullable = false, length = 150)
    private String rtoName;

    @Column(name = "state_code", nullable = false, length = 10)
    private String stateCode;

    @Column(name = "city_id", nullable = false)
    private Long cityId;

    public Rto() {
    }

    public String getRtoCode() {
        return rtoCode;
    }

    public void setRtoCode(String rtoCode) {
        this.rtoCode = rtoCode;
    }

    public String getRtoName() {
        return rtoName;
    }

    public void setRtoName(String rtoName) {
        this.rtoName = rtoName;
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
}