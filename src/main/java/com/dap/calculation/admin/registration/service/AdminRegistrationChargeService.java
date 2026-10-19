package com.dap.calculation.admin.registration.service;

import com.dap.calculation.admin.registration.dto.AdminRegistrationChargeRequestDTO;
import com.dap.calculation.admin.registration.dto.AdminRegistrationChargeResponseDTO;
import com.dap.calculation.entity.City;
import com.dap.calculation.entity.RegistrationCharge;
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

    // GET: All registration charges
    @Transactional(readOnly = true)
    public List<AdminRegistrationChargeResponseDTO> getAllRegistrationCharges() {

        return registrationChargeRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // GET: Registration charge by ID
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

    // GET: All charges for a state
    @Transactional(readOnly = true)
    public List<AdminRegistrationChargeResponseDTO> getRegistrationChargesByState(
            String stateCode) {

        String normalizedStateCode = normalizeStateCode(stateCode);
        validateStateExists(normalizedStateCode);

        return registrationChargeRepository
                .findByStateCode(normalizedStateCode)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // GET: State-level charges only
    @Transactional(readOnly = true)
    public List<AdminRegistrationChargeResponseDTO> getStateLevelCharges(
            String stateCode) {

        String normalizedStateCode = normalizeStateCode(stateCode);
        validateStateExists(normalizedStateCode);

        return registrationChargeRepository
                .findByStateCodeAndChargeScope(normalizedStateCode, "STATE")
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // GET: City-level charges only
    @Transactional(readOnly = true)
    public List<AdminRegistrationChargeResponseDTO> getCityLevelCharges(
            Long cityId) {

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found: " + cityId));

        return registrationChargeRepository
                .findByCityIdAndChargeScope(city.getCityId(), "CITY")
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // POST: Create registration charge
    public AdminRegistrationChargeResponseDTO createRegistrationCharge(
            AdminRegistrationChargeRequestDTO request) {

        RegistrationCharge charge = new RegistrationCharge();

        applyRequest(charge, request);

        RegistrationCharge savedCharge =
                registrationChargeRepository.save(charge);

        return toResponseDTO(savedCharge);
    }

    // PUT: Update registration charge
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

    // DELETE: Registration charge
    public void deleteRegistrationCharge(Long registrationChargeId) {

        RegistrationCharge charge = registrationChargeRepository
                .findById(registrationChargeId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Registration charge not found: " + registrationChargeId));

        registrationChargeRepository.delete(charge);
    }

    private void applyRequest(
            RegistrationCharge charge,
            AdminRegistrationChargeRequestDTO request) {

        if (request == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Request body is required");
        }

        String scope = normalize(request.getChargeScope())
                .toUpperCase(Locale.ROOT);

        String stateCode = normalizeStateCode(request.getStateCode());
        String chargeName = normalize(request.getChargeName());
        BigDecimal chargeValue = request.getChargeValue();
        Long cityId = request.getCityId();

        validateCommonFields(chargeName, chargeValue);
        validateStateExists(stateCode);

        if (!scope.equals("STATE") && !scope.equals("CITY")) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Charge scope must be STATE or CITY");
        }

        if ("STATE".equals(scope)) {

            if (cityId != null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "City ID must be null for a STATE-level charge");
            }

        } else {

            if (cityId == null) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "City ID is required for a CITY-level charge");
            }

            City city = cityRepository.findById(cityId)
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.BAD_REQUEST,
                            "City not found: " + cityId));

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

    private void validateCommonFields(
            String chargeName,
            BigDecimal chargeValue) {

        if (chargeName.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Charge name is required");
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
    }

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

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeStateCode(String value) {
        return normalize(value).toUpperCase(Locale.ROOT);
    }

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