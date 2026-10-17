package com.dap.calculation.admin.dashboard.controller;

import com.dap.calculation.admin.dashboard.dto.AdminDashboardResponseDTO;
import com.dap.calculation.admin.dashboard.service.AdminDashboardService;
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