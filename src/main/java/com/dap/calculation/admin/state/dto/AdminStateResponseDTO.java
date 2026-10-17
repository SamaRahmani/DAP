package com.dap.calculation.admin.state.dto;

public class AdminStateResponseDTO {

    private String stateCode;
    private String stateName;

    public AdminStateResponseDTO() {
    }

    public AdminStateResponseDTO(
            String stateCode,
            String stateName
    ) {
        this.stateCode = stateCode;
        this.stateName = stateName;
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