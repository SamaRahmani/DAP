package com.dap.calculation.dto;

import jakarta.validation.constraints.NotBlank;

public class DapCalculationRequest {

    @NotBlank(message = "Vehicle ID is required")
    private String vehicleId;

    @NotBlank(message = "RTO code is required")
    private String rtoCode;

    @NotBlank(message = "Insurance ID is required")
    private String insuranceId;

    public DapCalculationRequest() {
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRtoCode() {
        return rtoCode;
    }

    public void setRtoCode(String rtoCode) {
        this.rtoCode = rtoCode;
    }

    public String getInsuranceId() {
        return insuranceId;
    }

    public void setInsuranceId(String insuranceId) {
        this.insuranceId = insuranceId;
    }
}