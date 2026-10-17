package com.dap.calculation.admin.vehicle.service;

import com.dap.calculation.admin.vehicle.dto.AdminVehicleRequestDTO;
import com.dap.calculation.admin.vehicle.dto.AdminVehicleResponseDTO;
import com.dap.calculation.entity.Vehicle;
import com.dap.calculation.repository.VehicleRepository;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AdminVehicleService {

    private final VehicleRepository vehicleRepository;

    public AdminVehicleService(VehicleRepository vehicleRepository) {
        this.vehicleRepository = vehicleRepository;
    }

    // Get all vehicles
    public List<AdminVehicleResponseDTO> getAllVehicles() {
        return vehicleRepository.findAll()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    // Get a vehicle by ID
    public AdminVehicleResponseDTO getVehicleById(String vehicleId) {
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new RuntimeException("Vehicle not found with ID: " + vehicleId));

        return mapToResponseDTO(vehicle);
    }

    // Create a vehicle
    public AdminVehicleResponseDTO createVehicle(AdminVehicleRequestDTO requestDTO) {

        if (vehicleRepository.existsById(requestDTO.getVehicleId())) {
            throw new RuntimeException(
                    "Vehicle already exists with ID: " + requestDTO.getVehicleId());
        }

        Vehicle vehicle = mapToEntity(requestDTO);

        Vehicle savedVehicle = vehicleRepository.save(vehicle);

        return mapToResponseDTO(savedVehicle);
    }

    // Update a vehicle
    public AdminVehicleResponseDTO updateVehicle(
            String vehicleId,
            AdminVehicleRequestDTO requestDTO) {

        Vehicle existingVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new RuntimeException("Vehicle not found with ID: " + vehicleId));

        existingVehicle.setModelName(requestDTO.getModelName());
        existingVehicle.setVariantName(requestDTO.getVariantName());
        existingVehicle.setFuelType(requestDTO.getFuelType());
        existingVehicle.setTransmissionType(requestDTO.getTransmissionType());
        existingVehicle.setExShowroomPrice(requestDTO.getExShowroomPrice());
        existingVehicle.setLengthMm(requestDTO.getLengthMm());
        existingVehicle.setGroundClearanceMm(requestDTO.getGroundClearanceMm());

        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);

        return mapToResponseDTO(updatedVehicle);
    }

    // Partially update a vehicle
    public AdminVehicleResponseDTO patchVehicle(
            String vehicleId,
            AdminVehicleRequestDTO requestDTO) {

        Vehicle existingVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new RuntimeException("Vehicle not found with ID: " + vehicleId));

        if (requestDTO.getModelName() != null) {
            existingVehicle.setModelName(requestDTO.getModelName());
        }

        if (requestDTO.getVariantName() != null) {
            existingVehicle.setVariantName(requestDTO.getVariantName());
        }

        if (requestDTO.getFuelType() != null) {
            existingVehicle.setFuelType(requestDTO.getFuelType());
        }

        if (requestDTO.getTransmissionType() != null) {
            existingVehicle.setTransmissionType(requestDTO.getTransmissionType());
        }

        if (requestDTO.getExShowroomPrice() != null) {
            existingVehicle.setExShowroomPrice(requestDTO.getExShowroomPrice());
        }

        if (requestDTO.getLengthMm() != null) {
            existingVehicle.setLengthMm(requestDTO.getLengthMm());
        }

        if (requestDTO.getGroundClearanceMm() != null) {
            existingVehicle.setGroundClearanceMm(requestDTO.getGroundClearanceMm());
        }

        Vehicle updatedVehicle = vehicleRepository.save(existingVehicle);

        return mapToResponseDTO(updatedVehicle);
    }

    // Delete a vehicle
    public void deleteVehicle(String vehicleId) {

        Vehicle existingVehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() ->
                        new RuntimeException("Vehicle not found with ID: " + vehicleId));

        vehicleRepository.delete(existingVehicle);
    }

    // Convert request DTO to entity
    private Vehicle mapToEntity(AdminVehicleRequestDTO requestDTO) {

        Vehicle vehicle = new Vehicle();

        vehicle.setVehicleId(requestDTO.getVehicleId());
        vehicle.setModelName(requestDTO.getModelName());
        vehicle.setVariantName(requestDTO.getVariantName());
        vehicle.setFuelType(requestDTO.getFuelType());
        vehicle.setTransmissionType(requestDTO.getTransmissionType());
        vehicle.setExShowroomPrice(requestDTO.getExShowroomPrice());
        vehicle.setLengthMm(requestDTO.getLengthMm());
        vehicle.setGroundClearanceMm(requestDTO.getGroundClearanceMm());

        return vehicle;
    }

    // Convert entity to response DTO
    private AdminVehicleResponseDTO mapToResponseDTO(Vehicle vehicle) {

        AdminVehicleResponseDTO responseDTO = new AdminVehicleResponseDTO();

        responseDTO.setVehicleId(vehicle.getVehicleId());
        responseDTO.setModelName(vehicle.getModelName());
        responseDTO.setVariantName(vehicle.getVariantName());
        responseDTO.setFuelType(vehicle.getFuelType());
        responseDTO.setTransmissionType(vehicle.getTransmissionType());
        responseDTO.setExShowroomPrice(vehicle.getExShowroomPrice());
        responseDTO.setLengthMm(vehicle.getLengthMm());
        responseDTO.setGroundClearanceMm(vehicle.getGroundClearanceMm());

        return responseDTO;
    }
}