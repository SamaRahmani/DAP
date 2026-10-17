package com.dap.calculation.admin.state.dto;

public class AdminStateRequestDTO {

    private String stateCode;
    private String stateName;

    public AdminStateRequestDTO() {
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