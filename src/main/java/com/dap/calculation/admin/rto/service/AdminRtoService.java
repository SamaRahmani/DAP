package com.dap.calculation.admin.rto.service;

import com.dap.calculation.admin.rto.dto.AdminRtoRequestDTO;
import com.dap.calculation.admin.rto.dto.AdminRtoResponseDTO;
import com.dap.calculation.entity.City;
import com.dap.calculation.entity.Rto;
import com.dap.calculation.repository.CityRepository;
import com.dap.calculation.repository.RtoRepository;
import com.dap.calculation.repository.StateRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class AdminRtoService {

    private final RtoRepository rtoRepository;
    private final CityRepository cityRepository;
    private final StateRepository stateRepository;

    public AdminRtoService(
            RtoRepository rtoRepository,
            CityRepository cityRepository,
            StateRepository stateRepository) {

        this.rtoRepository = rtoRepository;
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
    }

    // GET: All RTOs
    @Transactional(readOnly = true)
    public List<AdminRtoResponseDTO> getAllRtos() {

        return rtoRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // GET: RTO by code
    @Transactional(readOnly = true)
    public AdminRtoResponseDTO getRtoByCode(String rtoCode) {

        Rto rto = rtoRepository.findById(normalize(rtoCode))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "RTO not found: " + rtoCode));

        return toResponseDTO(rto);
    }

    // GET: RTOs by city
    @Transactional(readOnly = true)
    public List<AdminRtoResponseDTO> getRtosByCityId(Long cityId) {

        if (!cityRepository.existsById(cityId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "City not found: " + cityId);
        }

        return rtoRepository.findByCityId(cityId)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // POST: Create RTO
    public AdminRtoResponseDTO createRto(AdminRtoRequestDTO request) {

        String rtoCode = normalize(request.getRtoCode());
        String rtoName = normalize(request.getRtoName());
        String stateCode = normalizeStateCode(request.getStateCode());

        validateRto(rtoCode, rtoName, stateCode, request.getCityId());

        if (rtoRepository.existsById(rtoCode)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "RTO code already exists: " + rtoCode);
        }

        validateLocation(stateCode, request.getCityId());

        Rto rto = new Rto();
        rto.setRtoCode(rtoCode);
        rto.setRtoName(rtoName);
        rto.setStateCode(stateCode);
        rto.setCityId(request.getCityId());

        try {
            return toResponseDTO(rtoRepository.saveAndFlush(rto));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to create RTO. Check for duplicate codes or invalid location references.");
        }
    }

    // PUT: Update RTO
    public AdminRtoResponseDTO updateRto(
            String rtoCode,
            AdminRtoRequestDTO request) {

        String normalizedCode = normalize(rtoCode);

        Rto rto = rtoRepository.findById(normalizedCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "RTO not found: " + normalizedCode));

        String rtoName = normalize(request.getRtoName());
        String stateCode = normalizeStateCode(request.getStateCode());

        validateRto(
                normalizedCode,
                rtoName,
                stateCode,
                request.getCityId());

        validateLocation(stateCode, request.getCityId());

        rto.setRtoName(rtoName);
        rto.setStateCode(stateCode);
        rto.setCityId(request.getCityId());

        try {
            return toResponseDTO(rtoRepository.saveAndFlush(rto));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to update RTO. Check its location references.");
        }
    }

    // DELETE: Delete RTO
    public void deleteRto(String rtoCode) {

        Rto rto = rtoRepository.findById(normalize(rtoCode))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "RTO not found: " + rtoCode));

        try {
            rtoRepository.delete(rto);
            rtoRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this RTO because other records depend on it.");
        }
    }

    private void validateLocation(String stateCode, Long cityId) {

        if (!stateRepository.existsById(stateCode)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "State does not exist: " + stateCode);
        }

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "City does not exist: " + cityId));

        if (!stateCode.equalsIgnoreCase(city.getStateCode())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "The selected city does not belong to state: " + stateCode);
        }
    }

    private void validateRto(
            String rtoCode,
            String rtoName,
            String stateCode,
            Long cityId) {

        if (rtoCode.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "RTO code is required");
        }

        if (rtoName.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "RTO name is required");
        }

        if (stateCode.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "State code is required");
        }

        if (cityId == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "City ID is required");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeStateCode(String value) {
        return normalize(value).toUpperCase();
    }

    private AdminRtoResponseDTO toResponseDTO(Rto rto) {

        return new AdminRtoResponseDTO(
                rto.getRtoCode(),
                rto.getRtoName(),
                rto.getStateCode(),
                rto.getCityId());
    }
}