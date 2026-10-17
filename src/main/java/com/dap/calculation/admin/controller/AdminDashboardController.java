package com.dap.calculation.admin.controller;

import com.dap.calculation.admin.dto.AdminDashboardResponseDTO;
import com.dap.calculation.admin.service.AdminDashboardService;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin/dashboard")
public class AdminDashboardController {

    private final AdminDashboardService adminDashboardService;

    public AdminDashboardController(
            AdminDashboardService adminDashboardService
    ) {
        this.adminDashboardService = adminDashboardService;
    }

    @GetMapping("/summary")
    public AdminDashboardResponseDTO getDashboardSummary() {

        return adminDashboardService.getDashboardSummary();
    }
}