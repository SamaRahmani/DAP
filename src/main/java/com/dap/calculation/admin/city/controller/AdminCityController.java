package com.dap.calculation.admin.city.controller;

import com.dap.calculation.admin.city.dto.AdminCityRequestDTO;
import com.dap.calculation.admin.city.dto.AdminCityResponseDTO;
import com.dap.calculation.admin.city.service.AdminCityService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminCityController {

    private final AdminCityService adminCityService;

    public AdminCityController(AdminCityService adminCityService) {
        this.adminCityService = adminCityService;
    }

    // GET: All cities
    @GetMapping("/cities")
    public List<AdminCityResponseDTO> getAllCities() {
        return adminCityService.getAllCities();
    }

    // GET: City by ID
    @GetMapping("/cities/{cityId}")
    public AdminCityResponseDTO getCityById(
            @PathVariable Long cityId) {

        return adminCityService.getCityById(cityId);
    }

    // GET: Cities by state
    @GetMapping("/states/{stateCode}/cities")
    public List<AdminCityResponseDTO> getCitiesByStateCode(
            @PathVariable String stateCode) {

        return adminCityService.getCitiesByStateCode(stateCode);
    }

    // POST: Create city
    @PostMapping("/cities")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminCityResponseDTO createCity(
            @RequestBody AdminCityRequestDTO request) {

        return adminCityService.createCity(request);
    }

    // PUT: Update city
    @PutMapping("/cities/{cityId}")
    public AdminCityResponseDTO updateCity(
            @PathVariable Long cityId,
            @RequestBody AdminCityRequestDTO request) {

        return adminCityService.updateCity(cityId, request);
    }

    // DELETE: Delete city
    @DeleteMapping("/cities/{cityId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCity(@PathVariable Long cityId) {

        adminCityService.deleteCity(cityId);
    }
}