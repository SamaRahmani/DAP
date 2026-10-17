package com.dap.calculation.admin.service;

import com.dap.calculation.admin.dto.AdminDashboardResponseDTO;
import com.dap.calculation.repository.InsuranceRepository;
import com.dap.calculation.repository.RtoRepository;
import com.dap.calculation.repository.VehicleRepository;

import org.springframework.stereotype.Service;

@Service
public class AdminDashboardService {

    private final VehicleRepository vehicleRepository;
    private final RtoRepository rtoRepository;
    private final InsuranceRepository insuranceRepository;

    public AdminDashboardService(
            VehicleRepository vehicleRepository,
            RtoRepository rtoRepository,
            InsuranceRepository insuranceRepository
    ) {
        this.vehicleRepository = vehicleRepository;
        this.rtoRepository = rtoRepository;
        this.insuranceRepository = insuranceRepository;
    }

    public AdminDashboardResponseDTO getDashboardSummary() {

        long totalVehicles = vehicleRepository.count();

        long totalRtoLocations = rtoRepository.count();

        long totalInsuranceProviders = insuranceRepository.count();

        return new AdminDashboardResponseDTO(
                totalVehicles,
                totalRtoLocations,
                totalInsuranceProviders
        );
    }
}