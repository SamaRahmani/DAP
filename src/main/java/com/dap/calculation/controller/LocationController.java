package com.dap.calculation.controller;

import com.dap.calculation.entity.State;
import com.dap.calculation.entity.City;
import com.dap.calculation.entity.Rto;

import com.dap.calculation.repository.StateRepository;
import com.dap.calculation.repository.CityRepository;
import com.dap.calculation.repository.RtoRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/locations")
public class LocationController {

    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final RtoRepository rtoRepository;

    public LocationController(
            StateRepository stateRepository,
            CityRepository cityRepository,
            RtoRepository rtoRepository) {

        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.rtoRepository = rtoRepository;
    }

    // Get all states
    @GetMapping("/states")
    public List<State> getAllStates() {
        return stateRepository.findAll();
    }

    // Get cities by state code
    @GetMapping("/cities/{stateCode}")
    public List<City> getCitiesByState(
            @PathVariable String stateCode) {

        return cityRepository.findByStateCode(stateCode);
    }

    // Get RTOs by city ID
    @GetMapping("/rtos/{cityId}")
    public List<Rto> getRtosByCity(
            @PathVariable Long cityId) {

        return rtoRepository.findByCityId(cityId);
    }
}