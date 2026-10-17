package com.dap.calculation.admin.insurance.service;

import com.dap.calculation.admin.insurance.dto.AdminInsuranceRequestDTO;
import com.dap.calculation.admin.insurance.dto.AdminInsuranceResponseDTO;
import com.dap.calculation.entity.Insurance;
import com.dap.calculation.repository.InsuranceRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class AdminInsuranceService {

    private final InsuranceRepository insuranceRepository;

    public AdminInsuranceService(InsuranceRepository insuranceRepository) {
        this.insuranceRepository = insuranceRepository;
    }

    // GET: All insurance providers
    @Transactional(readOnly = true)
    public List<AdminInsuranceResponseDTO> getAllInsuranceProviders() {

        return insuranceRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // GET: Provider by ID
    @Transactional(readOnly = true)
    public AdminInsuranceResponseDTO getInsuranceById(String insuranceId) {

        Insurance insurance = insuranceRepository.findById(normalize(insuranceId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Insurance provider not found: " + insuranceId));

        return toResponseDTO(insurance);
    }

    // POST: Create provider
    public AdminInsuranceResponseDTO createInsurance(
            AdminInsuranceRequestDTO request) {

        String insuranceId = normalize(request.getInsuranceId());
        String providerName = normalize(request.getProviderName());
        BigDecimal percentage = request.getPercentage();

        validateInsurance(insuranceId, providerName, percentage);

        if (insuranceRepository.existsById(insuranceId)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insurance ID already exists: " + insuranceId);
        }

        if (insuranceRepository.existsByProviderNameIgnoreCase(providerName)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insurance provider name already exists: " + providerName);
        }

        Insurance insurance = new Insurance();
        insurance.setInsuranceId(insuranceId);
        insurance.setProviderName(providerName);
        insurance.setPercentage(percentage);

        try {
            return toResponseDTO(insuranceRepository.saveAndFlush(insurance));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to create insurance provider. Check for duplicate values.");
        }
    }

    // PUT: Update provider
    public AdminInsuranceResponseDTO updateInsurance(
            String insuranceId,
            AdminInsuranceRequestDTO request) {

        String normalizedId = normalize(insuranceId);

        Insurance insurance = insuranceRepository.findById(normalizedId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Insurance provider not found: " + normalizedId));

        String providerName = normalize(request.getProviderName());
        BigDecimal percentage = request.getPercentage();

        validateInsurance(normalizedId, providerName, percentage);

        boolean duplicateName = insuranceRepository
                .existsByProviderNameIgnoreCase(providerName);

        if (duplicateName
                && !insurance.getProviderName().equalsIgnoreCase(providerName)) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Insurance provider name already exists: " + providerName);
        }

        insurance.setProviderName(providerName);
        insurance.setPercentage(percentage);

        try {
            return toResponseDTO(insuranceRepository.saveAndFlush(insurance));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Unable to update insurance provider.");
        }
    }

    // DELETE: Provider
    public void deleteInsurance(String insuranceId) {

        Insurance insurance = insuranceRepository.findById(normalize(insuranceId))
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Insurance provider not found: " + insuranceId));

        try {
            insuranceRepository.delete(insurance);
            insuranceRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this insurance provider because other records depend on it.");
        }
    }

    private void validateInsurance(
            String insuranceId,
            String providerName,
            BigDecimal percentage) {

        if (insuranceId.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insurance ID is required");
        }

        if (providerName.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insurance provider name is required");
        }

        if (percentage == null) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insurance percentage is required");
        }

        if (percentage.compareTo(BigDecimal.ZERO) < 0
                || percentage.compareTo(new BigDecimal("100")) > 0) {

            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insurance percentage must be between 0 and 100");
        }

        if (percentage.scale() > 4) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Insurance percentage supports up to 4 decimal places");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private AdminInsuranceResponseDTO toResponseDTO(Insurance insurance) {

        return new AdminInsuranceResponseDTO(
                insurance.getInsuranceId(),
                insurance.getProviderName(),
                insurance.getPercentage());
    }
}