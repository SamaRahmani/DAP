
package com.dap.calculation.admin.registration.service;

import com.dap.calculation.admin.registration.dto.AdminRegistrationChargeRequestDTO;
import com.dap.calculation.admin.registration.dto.AdminRegistrationChargeResponseDTO;
import com.dap.calculation.entity.City;
import com.dap.calculation.entity.RegistrationCharge;
import com.dap.calculation.enums.ChargeScope;
import com.dap.calculation.repository.CityRepository;
import com.dap.calculation.repository.RegistrationChargeRepository;
import com.dap.calculation.repository.StateRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class AdminRegistrationChargeService {

    private final RegistrationChargeRepository registrationChargeRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;

    public AdminRegistrationChargeService(
            RegistrationChargeRepository registrationChargeRepository,
            StateRepository stateRepository,
            CityRepository cityRepository) {

        this.registrationChargeRepository = registrationChargeRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
    }

    // =====================================
    // GET: ALL REGISTRATION CHARGES
    // =====================================

    @Transactional(readOnly = true)
    public List<AdminRegistrationChargeResponseDTO> getAllRegistrationCharges() {

        return registrationChargeRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // =====================================
    // GET: REGISTRATION CHARGE BY ID
    // =====================================

    @Transactional(readOnly = true)
    public AdminRegistrationChargeResponseDTO getRegistrationChargeById(
            Long registrationChargeId) {

        RegistrationCharge charge = registrationChargeRepository
                .findById(registrationChargeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration charge not found: " + registrationChargeId));

        return toResponseDTO(charge);
    }

    // =====================================
    // GET: ALL APPLICABLE CHARGES FOR STATE
    // Includes NATIONAL and STATE/CITY charges
    // =====================================

    @Transactional(readOnly = true)
    public List<AdminRegistrationChargeResponseDTO> getRegistrationChargesByState(
            String stateCode) {

        String normalizedStateCode = normalizeStateCode(stateCode);
        validateStateExists(normalizedStateCode);

        return registrationChargeRepository.findAll()
                .stream()
                .filter(charge ->
                        charge.getChargeScope() == ChargeScope.NATIONAL ||
                                normalizedStateCode.equals(charge.getStateCode()))
                .map(this::toResponseDTO)
                .toList();
    }

    // =====================================
    // GET: STATE-LEVEL CHARGES ONLY
    // =====================================

    @Transactional(readOnly = true)
    public List<AdminRegistrationChargeResponseDTO> getStateLevelCharges(
            String stateCode) {

        String normalizedStateCode = normalizeStateCode(stateCode);
        validateStateExists(normalizedStateCode);

        return registrationChargeRepository
                .findByStateCodeAndChargeScope(
                        normalizedStateCode,
                        ChargeScope.STATE)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // =====================================
    // GET: CITY-LEVEL CHARGES ONLY
    // =====================================

    @Transactional(readOnly = true)
    public List<AdminRegistrationChargeResponseDTO> getCityLevelCharges(
            Long cityId) {

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found: " + cityId));

        return registrationChargeRepository
                .findByCityIdAndChargeScope(
                        city.getCityId(),
                        ChargeScope.CITY)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // =====================================
    // POST: CREATE REGISTRATION CHARGE
    // =====================================

    public AdminRegistrationChargeResponseDTO createRegistrationCharge(
            AdminRegistrationChargeRequestDTO request) {

        RegistrationCharge charge = new RegistrationCharge();

        applyRequest(charge, request);

        RegistrationCharge savedCharge =
                registrationChargeRepository.save(charge);

        return toResponseDTO(savedCharge);
    }

    // =====================================
    // PUT: UPDATE REGISTRATION CHARGE
    // =====================================

    public AdminRegistrationChargeResponseDTO updateRegistrationCharge(
            Long registrationChargeId,
            AdminRegistrationChargeRequestDTO request) {

        RegistrationCharge charge = registrationChargeRepository
                .findById(registrationChargeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration charge not found: " + registrationChargeId));

        applyRequest(charge, request);

        RegistrationCharge updatedCharge =
                registrationChargeRepository.save(charge);

        return toResponseDTO(updatedCharge);
    }

    // =====================================
    // DELETE: REGISTRATION CHARGE
    // =====================================

    public void deleteRegistrationCharge(Long registrationChargeId) {

        RegistrationCharge charge = registrationChargeRepository
                .findById(registrationChargeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration charge not found: " + registrationChargeId));

        registrationChargeRepository.delete(charge);
    }

    // =====================================
    // APPLY REQUEST AND VALIDATE SCOPE
    // =====================================

    private void applyRequest(
            RegistrationCharge charge,
            AdminRegistrationChargeRequestDTO request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Request body is required");
        }

        ChargeScope scope = request.getChargeScope();

        String stateCode = normalizeStateCode(request.getStateCode());
        String chargeName = normalize(request.getChargeName());
        BigDecimal chargeValue = request.getChargeValue();
        Long cityId = request.getCityId();

        validateCommonFields(chargeName, chargeValue);

        if (scope == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Charge scope is required");
        }

        // NATIONAL SCOPE
        if (scope == ChargeScope.NATIONAL) {

            if (!stateCode.isEmpty() || cityId != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "State code and city ID must be null for a NATIONAL charge");
            }

            stateCode = null;
            cityId = null;
        }

        // STATE SCOPE
        else if (scope == ChargeScope.STATE) {

            validateStateExists(stateCode);

            if (cityId != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "City ID must be null for a STATE-level charge");
            }

            cityId = null;
        }


// CITY SCOPE
        else if (scope == ChargeScope.CITY) {

            validateStateExists(stateCode);

            if (cityId == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "City ID is required for a CITY-level charge");
            }

            final Long selectedCityId = cityId;

            City city = cityRepository.findById(selectedCityId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "City not found: " + selectedCityId));

            if (!stateCode.equalsIgnoreCase(city.getStateCode())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "The selected city does not belong to state: "
                                + stateCode);
            }
        }


        charge.setChargeScope(scope);
        charge.setStateCode(stateCode);
        charge.setCityId(cityId);
        charge.setChargeName(chargeName);
        charge.setChargeValue(chargeValue);
    }

    // =====================================
    // COMMON FIELD VALIDATION
    // =====================================

    private void validateCommonFields(
            String chargeName,
            BigDecimal chargeValue) {

        if (chargeName.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Charge name is required");
        }

        if (chargeName.length() > 100) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Charge name cannot exceed 100 characters");
        }

        if (chargeValue == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Charge value is required");
        }

        if (chargeValue.compareTo(BigDecimal.ZERO) < 0) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Charge value cannot be negative");
        }

        if (chargeValue.scale() > 4) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Charge value supports up to 4 decimal places");
        }

        if (chargeValue.precision() - chargeValue.scale() > 11) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Charge value cannot exceed 11 integer digits");
        }
    }

    // =====================================
    // STATE VALIDATION
    // =====================================

    private void validateStateExists(String stateCode) {

        if (stateCode.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "State code is required");
        }

        if (!stateRepository.existsById(stateCode)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "State does not exist: " + stateCode);
        }
    }

    // =====================================
    // NORMALIZATION
    // =====================================

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeStateCode(String value) {
        return normalize(value).toUpperCase(Locale.ROOT);
    }

    // =====================================
    // ENTITY TO RESPONSE DTO
    // =====================================

    private AdminRegistrationChargeResponseDTO toResponseDTO(
            RegistrationCharge charge) {

        return new AdminRegistrationChargeResponseDTO(
                charge.getRegistrationChargeId(),
                charge.getChargeScope(),
                charge.getStateCode(),
                charge.getCityId(),
                charge.getChargeName(),
                charge.getChargeValue());
    }
}
