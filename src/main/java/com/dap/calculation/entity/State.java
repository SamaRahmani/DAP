package com.dap.calculation.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "state")
public class State {

    @Id
    @Column(name = "state_code", length = 10)
    private String stateCode;

    @Column(name = "state_name", nullable = false, unique = true, length = 100)
    private String stateName;

    public State() {
    }

    public String getStateCode() {
        return stateCode;
    }

    public void setStateCode(String stateCode) {
        this.stateCode = stateCode;
    }

    public String getStateName() {
        return stateName;
    }

    public void setStateName(String stateName) {
        this.stateName = stateName;
    }
}