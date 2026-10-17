package com.dap.calculation.admin.dto;

public class AdminDashboardResponseDTO {

    private long totalVehicles;
    private long totalRtoLocations;
    private long totalInsuranceProviders;

    public AdminDashboardResponseDTO() {
    }

    public AdminDashboardResponseDTO(
            long totalVehicles,
            long totalRtoLocations,
            long totalInsuranceProviders
    ) {
        this.totalVehicles = totalVehicles;
        this.totalRtoLocations = totalRtoLocations;
        this.totalInsuranceProviders = totalInsuranceProviders;
    }

    public long getTotalVehicles() {
        return totalVehicles;
    }

    public void setTotalVehicles(long totalVehicles) {
        this.totalVehicles = totalVehicles;
    }

    public long getTotalRtoLocations() {
        return totalRtoLocations;
    }

    public void setTotalRtoLocations(long totalRtoLocations) {
        this.totalRtoLocations = totalRtoLocations;
    }

    public long getTotalInsuranceProviders() {
        return totalInsuranceProviders;
    }

    public void setTotalInsuranceProviders(long totalInsuranceProviders) {
        this.totalInsuranceProviders = totalInsuranceProviders;
    }
}