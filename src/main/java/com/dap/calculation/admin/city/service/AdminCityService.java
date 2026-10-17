package com.dap.calculation.admin.city.service;

import com.dap.calculation.admin.city.dto.AdminCityRequestDTO;
import com.dap.calculation.admin.city.dto.AdminCityResponseDTO;
import com.dap.calculation.entity.City;
import com.dap.calculation.repository.CityRepository;
import com.dap.calculation.repository.StateRepository;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@Transactional
public class AdminCityService {

    private final CityRepository cityRepository;
    private final StateRepository stateRepository;

    public AdminCityService(
            CityRepository cityRepository,
            StateRepository stateRepository) {
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
    }

    // GET: All cities
    @Transactional(readOnly = true)
    public List<AdminCityResponseDTO> getAllCities() {

        return cityRepository.findAll()
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // GET: City by ID
    @Transactional(readOnly = true)
    public AdminCityResponseDTO getCityById(Long cityId) {

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found with ID: " + cityId));

        return toResponseDTO(city);
    }

    // GET: Cities by state code
    @Transactional(readOnly = true)
    public List<AdminCityResponseDTO> getCitiesByStateCode(String stateCode) {

        String normalizedStateCode = normalizeStateCode(stateCode);

        if (!stateRepository.existsById(normalizedStateCode)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "State not found: " + normalizedStateCode);
        }

        return cityRepository.findByStateCode(normalizedStateCode)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    // POST: Create city
    public AdminCityResponseDTO createCity(AdminCityRequestDTO request) {

        String cityName = normalize(request.getCityName());
        String stateCode = normalizeStateCode(request.getStateCode());

        validateCity(cityName, stateCode);

        if (!stateRepository.existsById(stateCode)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot create city. State does not exist: " + stateCode);
        }

        City city = new City();
        city.setCityName(cityName);
        city.setStateCode(stateCode);

        City savedCity = cityRepository.save(city);

        return toResponseDTO(savedCity);
    }

    // PUT: Update city
    public AdminCityResponseDTO updateCity(
            Long cityId,
            AdminCityRequestDTO request) {

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found with ID: " + cityId));

        String cityName = normalize(request.getCityName());
        String stateCode = normalizeStateCode(request.getStateCode());

        validateCity(cityName, stateCode);

        if (!stateRepository.existsById(stateCode)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Cannot update city. State does not exist: " + stateCode);
        }

        city.setCityName(cityName);
        city.setStateCode(stateCode);

        City updatedCity = cityRepository.save(city);

        return toResponseDTO(updatedCity);
    }

    // DELETE: Delete city
    public void deleteCity(Long cityId) {

        City city = cityRepository.findById(cityId)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "City not found with ID: " + cityId));

        try {
            cityRepository.delete(city);
            cityRepository.flush();
        } catch (org.springframework.dao.DataIntegrityViolationException exception) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Cannot delete this city because other records depend on it.");
        }
    }

    private void validateCity(String cityName, String stateCode) {

        if (cityName.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "City name is required");
        }

        if (stateCode.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "State code is required");
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeStateCode(String stateCode) {
        return normalize(stateCode).toUpperCase();
    }

    private AdminCityResponseDTO toResponseDTO(City city) {

        return new AdminCityResponseDTO(
                city.getCityId(),
                city.getCityName(),
                city.getStateCode());
    }
}