
package com.dap.calculation.dto;

import java.math.BigDecimal;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class DapCalculationRequest {

    @Valid
    private VehicleDetails vehicle;

    @DecimalMin(
            value = "0.01",
            message = "Ex-showroom price must be greater than zero"
    )
    private BigDecimal exShowroomPrice;

    @Valid
    @NotNull(message = "Location details are required")
    private LocationDetails location;

    @NotBlank(message = "Insurance provider is required")
    private String insuranceProvider;

    public VehicleDetails getVehicle() {
        return vehicle;
    }

    public void setVehicle(VehicleDetails vehicle) {
        this.vehicle = vehicle;
    }

    public BigDecimal getExShowroomPrice() {
        return exShowroomPrice;
    }

    public void setExShowroomPrice(BigDecimal exShowroomPrice) {
        this.exShowroomPrice = exShowroomPrice;
    }

    public LocationDetails getLocation() {
        return location;
    }

    public void setLocation(LocationDetails location) {
        this.location = location;
    }

    public String getInsuranceProvider() {
        return insuranceProvider;
    }

    public void setInsuranceProvider(String insuranceProvider) {
        this.insuranceProvider = insuranceProvider;
    }

    public static class VehicleDetails {

        @NotBlank(message = "Vehicle model name is required")
        private String modelName;

        @NotBlank(message = "Vehicle variant name is required")
        private String variantName;

        public String getModelName() {
            return modelName;
        }

        public void setModelName(String modelName) {
            this.modelName = modelName;
        }

        public String getVariantName() {
            return variantName;
        }

        public void setVariantName(String variantName) {
            this.variantName = variantName;
        }
    }

    public static class LocationDetails {

        @NotBlank(message = "State name is required")
        private String state;

        @NotBlank(message = "City name is required")
        private String city;

        @NotBlank(message = "RTO code is required")
        private String rtoCode;

        public String getState() {
            return state;
        }

        public void setState(String state) {
            this.state = state;
        }

        public String getCity() {
            return city;
        }

        public void setCity(String city) {
            this.city = city;
        }

        public String getRtoCode() {
            return rtoCode;
        }

        public void setRtoCode(String rtoCode) {
            this.rtoCode = rtoCode;
        }
    }
}
