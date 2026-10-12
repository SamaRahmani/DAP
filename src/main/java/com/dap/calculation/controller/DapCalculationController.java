package com.dap.calculation.controller;

import com.dap.calculation.dto.DapCalculationRequest;
import com.dap.calculation.dto.DapCalculationResponse;
import com.dap.calculation.service.DapCalculationService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/dap")
public class DapCalculationController {

    private final DapCalculationService dapCalculationService;

    public DapCalculationController(
            DapCalculationService dapCalculationService) {

        this.dapCalculationService = dapCalculationService;
    }

    @PostMapping("/calculate")
    public DapCalculationResponse calculate(
            @RequestBody DapCalculationRequest request) {

        return dapCalculationService.calculate(request);
    }
}