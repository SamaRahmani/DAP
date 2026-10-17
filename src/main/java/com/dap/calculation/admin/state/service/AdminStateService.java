package com.dap.calculation.admin.state.service;

import com.dap.calculation.admin.state.dto.AdminStateRequestDTO;
import com.dap.calculation.admin.state.dto.AdminStateResponseDTO;
import com.dap.calculation.entity.State;
import com.dap.calculation.repository.StateRepository;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class AdminStateService {

    private final StateRepository stateRepository;

    public AdminStateService(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @Transactional(readOnly = true)
    public List<AdminStateResponseDTO> getAllStates() {

        return stateRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public AdminStateResponseDTO getStateByCode(String stateCode) {

        State state = stateRepository.findById(stateCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "State not found: " + stateCode
                ));

        return toResponseDTO(state);
    }

    public AdminStateResponseDTO createState(
            AdminStateRequestDTO request
    ) {

        String stateCode = normalize(request.getStateCode());
        String stateName = normalize(request.getStateName());

        validateState(stateCode, stateName);

        if (stateRepository.existsById(stateCode)) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "State code already exists: " + stateCode
            );
        }

        State state = new State();
        state.setStateCode(stateCode);
        state.setStateName(stateName);

        try {
            return toResponseDTO(stateRepository.saveAndFlush(state));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A state with this code or name already exists"
            );
        }
    }

    public AdminStateResponseDTO updateState(
            String stateCode,
            AdminStateRequestDTO request
    ) {

        String normalizedCode = normalize(stateCode);

        State state = stateRepository.findById(normalizedCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "State not found: " + normalizedCode
                ));

        String stateName = normalize(request.getStateName());

        if (stateName.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "State name is required"
            );
        }

        state.setStateName(stateName);

        try {
            return toResponseDTO(stateRepository.saveAndFlush(state));
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A state with this name already exists"
            );
        }
    }

    public void deleteState(String stateCode) {

        String normalizedCode = normalize(stateCode);

        State state = stateRepository.findById(normalizedCode)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "State not found: " + normalizedCode
                ));

        try {
            stateRepository.delete(state);
            stateRepository.flush();
        } catch (DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this state because cities or other records depend on it"
            );
        }
    }

    private void validateState(
            String stateCode,
            String stateName
    ) {

        if (stateCode.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "State code is required"
            );
        }

        if (stateName.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "State name is required"
            );
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private AdminStateResponseDTO toResponseDTO(State state) {

        return new AdminStateResponseDTO(
                state.getStateCode(),
                state.getStateName()
        );
    }
}