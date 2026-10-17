package com.dap.calculation.admin.rto.controller;

import com.dap.calculation.admin.rto.dto.AdminRtoRequestDTO;
import com.dap.calculation.admin.rto.dto.AdminRtoResponseDTO;
import com.dap.calculation.admin.rto.service.AdminRtoService;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminRtoController {

    private final AdminRtoService adminRtoService;

    public AdminRtoController(AdminRtoService adminRtoService) {
        this.adminRtoService = adminRtoService;
    }

    // GET: All RTOs
    @GetMapping("/rtos")
    public List<AdminRtoResponseDTO> getAllRtos() {
        return adminRtoService.getAllRtos();
    }

    // GET: RTO by code
    @GetMapping("/rtos/{rtoCode}")
    public AdminRtoResponseDTO getRtoByCode(
            @PathVariable String rtoCode) {

        return adminRtoService.getRtoByCode(rtoCode);
    }

    // GET: RTOs by city
    @GetMapping("/cities/{cityId}/rtos")
    public List<AdminRtoResponseDTO> getRtosByCityId(
            @PathVariable Long cityId) {

        return adminRtoService.getRtosByCityId(cityId);
    }

    // POST: Create RTO
    @PostMapping("/rtos")
    @ResponseStatus(HttpStatus.CREATED)
    public AdminRtoResponseDTO createRto(
            @RequestBody AdminRtoRequestDTO request) {

        return adminRtoService.createRto(request);
    }

    // PUT: Update RTO
    @PutMapping("/rtos/{rtoCode}")
    public AdminRtoResponseDTO updateRto(
            @PathVariable String rtoCode,
            @RequestBody AdminRtoRequestDTO request) {

        return adminRtoService.updateRto(rtoCode, request);
    }

    // DELETE: Delete RTO
    @DeleteMapping("/rtos/{rtoCode}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteRto(@PathVariable String rtoCode) {

        adminRtoService.deleteRto(rtoCode);
    }
}