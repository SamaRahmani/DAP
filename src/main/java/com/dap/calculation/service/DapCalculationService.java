package com.dap.calculation.service;

import com.dap.calculation.dto.DapCalculationRequest;
import com.dap.calculation.dto.DapCalculationResponse;

import com.dap.calculation.entity.Insurance;
import com.dap.calculation.entity.RegistrationCharge;
import com.dap.calculation.entity.Rto;
import com.dap.calculation.entity.State;
import com.dap.calculation.entity.TaxRule;
import com.dap.calculation.entity.Vehicle;

import com.dap.calculation.repository.InsuranceRepository;
import com.dap.calculation.repository.RegistrationChargeRepository;
import com.dap.calculation.repository.RtoRepository;
import com.dap.calculation.repository.StateRepository;
import com.dap.calculation.repository.TaxRuleRepository;
import com.dap.calculation.repository.VehicleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class DapCalculationService {

    private final VehicleRepository vehicleRepository;
    private final RtoRepository rtoRepository;
    private final StateRepository stateRepository;
    private final InsuranceRepository insuranceRepository;
    private final TaxRuleRepository taxRuleRepository;
    private final RegistrationChargeRepository registrationChargeRepository;

    public DapCalculationService(
            VehicleRepository vehicleRepository,
            RtoRepository rtoRepository,
            StateRepository stateRepository,
            InsuranceRepository insuranceRepository,
            TaxRuleRepository taxRuleRepository,
            RegistrationChargeRepository registrationChargeRepository) {

        this.vehicleRepository = vehicleRepository;
        this.rtoRepository = rtoRepository;
        this.stateRepository = stateRepository;
        this.insuranceRepository = insuranceRepository;
        this.taxRuleRepository = taxRuleRepository;
        this.registrationChargeRepository = registrationChargeRepository;
    }

    public DapCalculationResponse calculate(DapCalculationRequest request) {

        // 1. Fetch vehicle details
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Vehicle not found"));

        BigDecimal exShowroomPrice = vehicle.getExShowroomPrice();


        // 2. Fetch RTO details
        Rto rto = rtoRepository.findById(request.getRtoCode())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "RTO not found"));


        // 3. Fetch state associated with the selected RTO
        State state = stateRepository.findById(rto.getStateCode())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "State not found"));


        // 4. Fetch selected insurance provider
        Insurance insurance = insuranceRepository.findById(request.getInsuranceId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Insurance provider not found"));


        // 5. Fetch tax rules for the selected state
        List<TaxRule> taxRules =
                taxRuleRepository.findByStateCode(state.getStateCode());

        BigDecimal roadTaxPercentage = BigDecimal.ZERO;
        BigDecimal tcsPercentage = BigDecimal.ZERO;
        BigDecimal cessPercentage = BigDecimal.ZERO;

        for (TaxRule rule : taxRules) {

            if (rule.getTaxTypeId().equals("TAX01")) {
                roadTaxPercentage = rule.getTaxPercentage();
            } else if (rule.getTaxTypeId().equals("TAX02")) {
                tcsPercentage = rule.getTaxPercentage();
            } else if (rule.getTaxTypeId().equals("TAX03")) {
                cessPercentage = rule.getTaxPercentage();
            }
        }


        // 6. Calculate road tax
        BigDecimal roadTax = exShowroomPrice
                .multiply(roadTaxPercentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);


        // 7. Calculate TCS only when ex-showroom price is greater than ₹10 lakh
        BigDecimal tcs = BigDecimal.ZERO;

        if (exShowroomPrice.compareTo(new BigDecimal("1000000")) > 0) {

            tcs = exShowroomPrice
                    .multiply(tcsPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }


        // 8. Calculate cess only when ex-showroom price is greater than ₹25 lakh
        BigDecimal cess = BigDecimal.ZERO;

        if (exShowroomPrice.compareTo(new BigDecimal("2500000")) > 0) {

            cess = exShowroomPrice
                    .multiply(cessPercentage)
                    .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
        }


        // 9. Calculate total tax
        BigDecimal totalTax = roadTax
                .add(tcs)
                .add(cess);


        // 10. Calculate insurance premium
        BigDecimal insurancePremium = exShowroomPrice
                .multiply(insurance.getPercentage())
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);


        // 11. Fetch registration charges for the selected state
        List<RegistrationCharge> charges =
                registrationChargeRepository.findByStateCode(state.getStateCode());

        BigDecimal registrationCharges = BigDecimal.ZERO;

        for (RegistrationCharge charge : charges) {

            registrationCharges = registrationCharges.add(
                    charge.getChargeValue()
            );
        }


        // 12. Calculate final Drive Away Price
        BigDecimal totalDap = exShowroomPrice
                .add(totalTax)
                .add(insurancePremium)
                .add(registrationCharges);


        // 13. Prepare response
        DapCalculationResponse response = new DapCalculationResponse();

        response.setVehicleId(vehicle.getVehicleId());
        response.setModelName(vehicle.getModelName());
        response.setVariantName(vehicle.getVariantName());

        response.setRtoCode(rto.getRtoCode());
        response.setStateCode(state.getStateCode());

        response.setInsuranceProvider(insurance.getProviderName());

        response.setExShowroomPrice(exShowroomPrice);
        response.setRoadTax(roadTax);
        response.setTcs(tcs);
        response.setCess(cess);
        response.setRegistrationCharges(registrationCharges);
        response.setInsurancePremium(insurancePremium);
        response.setTotalDriveAwayPrice(totalDap);

        return response;
    }
}