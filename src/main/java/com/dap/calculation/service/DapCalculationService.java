
package com.dap.calculation.service;

import com.dap.calculation.dto.DapCalculationRequest;
import com.dap.calculation.dto.DapCalculationResponse;

import com.dap.calculation.entity.City;
import com.dap.calculation.entity.Insurance;
import com.dap.calculation.entity.RegistrationCharge;
import com.dap.calculation.entity.Rto;
import com.dap.calculation.entity.State;
import com.dap.calculation.entity.Vehicle;

import com.dap.calculation.enums.ChargeScope;

import com.dap.calculation.repository.CityRepository;
import com.dap.calculation.repository.InsuranceRepository;
import com.dap.calculation.repository.RegistrationChargeRepository;
import com.dap.calculation.repository.RtoRepository;
import com.dap.calculation.repository.StateRepository;
import com.dap.calculation.repository.VehicleRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class DapCalculationService {

    private final VehicleRepository vehicleRepository;
    private final RtoRepository rtoRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final InsuranceRepository insuranceRepository;
    private final RegistrationChargeRepository registrationChargeRepository;

    public DapCalculationService(
            VehicleRepository vehicleRepository,
            RtoRepository rtoRepository,
            StateRepository stateRepository,
            CityRepository cityRepository,
            InsuranceRepository insuranceRepository,
            RegistrationChargeRepository registrationChargeRepository) {

        this.vehicleRepository = vehicleRepository;
        this.rtoRepository = rtoRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.insuranceRepository = insuranceRepository;
        this.registrationChargeRepository = registrationChargeRepository;
    }

    @Transactional(readOnly = true)
    public DapCalculationResponse calculate(DapCalculationRequest request) {

        // 1. Validate price source

        boolean hasVehicle = request.getVehicle() != null;
        boolean hasManualPrice = request.getExShowroomPrice() != null;

        if (hasVehicle == hasManualPrice) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Provide either vehicle details or manual ex-showroom price, not both."
            );
        }

        // 2. Resolve vehicle or manual ex-showroom price

        Vehicle vehicle = null;
        BigDecimal exShowroomPrice;

        if (hasVehicle) {

            String modelName = request.getVehicle().getModelName().trim();
            String variantName = request.getVehicle().getVariantName().trim();

            vehicle = vehicleRepository
                    .findFirstByModelNameIgnoreCaseAndVariantNameIgnoreCase(
                            modelName,
                            variantName
                    )
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Vehicle model or variant not found."
                    ));

            exShowroomPrice = vehicle.getExShowroomPrice();

        } else {

            exShowroomPrice = request.getExShowroomPrice();

            if (exShowroomPrice.compareTo(BigDecimal.ZERO) <= 0) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Ex-showroom price must be greater than zero."
                );
            }
        }

        if (exShowroomPrice == null
                || exShowroomPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "A valid ex-showroom price is not available."
            );
        }

        // 3. Resolve state using state name

        String stateName = request.getLocation().getState().trim();

        State state = stateRepository
                .findByStateNameIgnoreCase(stateName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "State not found: " + stateName
                ));

        // 4. Resolve city using city name and state

        String cityName = request.getLocation().getCity().trim();

        City city = cityRepository
                .findByCityNameIgnoreCaseAndStateCode(
                        cityName,
                        state.getStateCode()
                )
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found in the selected state: " + cityName
                ));

        // 5. Resolve and validate RTO

        String rtoCode = request.getLocation().getRtoCode().trim();

        Rto rto = rtoRepository.findById(rtoCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "RTO not found: " + rtoCode
                ));

        if (!rto.getStateCode().equalsIgnoreCase(state.getStateCode())
                || !rto.getCityId().equals(city.getCityId())) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Selected RTO does not belong to the selected city and state."
            );
        }

        // 6. Resolve insurance provider using provider name

        String providerName = request.getInsuranceProvider().trim();

        Insurance insurance = insuranceRepository
                .findByProviderNameIgnoreCase(providerName)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Insurance provider not found: " + providerName
                ));

        // 7. Calculate insurance premium

        BigDecimal insurancePremium = exShowroomPrice
                .multiply(insurance.getPercentage())
                .divide(
                        new BigDecimal("100"),
                        2,
                        RoundingMode.HALF_UP
                );

        // 8. Calculate national registration charges

        List<RegistrationCharge> nationalRecords =
                registrationChargeRepository.findByChargeScope(
                        ChargeScope.NATIONAL
                );

        BigDecimal nationalCharges =
                calculateChargeTotal(nationalRecords);

        // 9. Calculate state registration charges

        List<RegistrationCharge> stateRecords =
                registrationChargeRepository.findByStateCodeAndChargeScope(
                        state.getStateCode(),
                        ChargeScope.STATE
                );

        BigDecimal stateCharges =
                calculateChargeTotal(stateRecords);

        // 10. Calculate city registration charges

        List<RegistrationCharge> cityRecords =
                registrationChargeRepository.findByCityIdAndChargeScope(
                        city.getCityId(),
                        ChargeScope.CITY
                );

        BigDecimal cityCharges =
                calculateChargeTotal(cityRecords);

        // 11. Calculate total registration charges

        BigDecimal totalRegistrationCharges = nationalCharges
                .add(stateCharges)
                .add(cityCharges);

        // 12. Calculate final DAP (taxes excluded)

        BigDecimal totalDap = exShowroomPrice
                .add(insurancePremium)
                .add(totalRegistrationCharges);

        // 13. Prepare response

        DapCalculationResponse response = new DapCalculationResponse();
        response.setModelName(
                vehicle != null ? vehicle.getModelName() : null
        );

        response.setVariantName(
                vehicle != null ? vehicle.getVariantName() : null
        );

        response.setStateName(state.getStateName());
        response.setCityName(city.getCityName());
        response.setRtoCode(rto.getRtoCode());


        response.setInsuranceProvider(insurance.getProviderName());
        response.setInsurancePercentage(insurance.getPercentage());

        response.setExShowroomPrice(exShowroomPrice);
        response.setInsurancePremium(insurancePremium);

        response.setNationalCharges(nationalCharges);
        response.setStateCharges(stateCharges);
        response.setCityCharges(cityCharges);

        response.setRegistrationCharges(totalRegistrationCharges);
        response.setTotalDriveAwayPrice(totalDap);

        return response;
    }

    // Helper method to sum registration charges

    private BigDecimal calculateChargeTotal(
            List<RegistrationCharge> charges) {

        return charges.stream()
                .map(RegistrationCharge::getChargeValue)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
