
package com.dap.calculation.repository;

import com.dap.calculation.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {

    @Query("""
            SELECT DISTINCT v.fuelType
            FROM Vehicle v
            WHERE v.fuelType IS NOT NULL
            ORDER BY v.fuelType
            """)
    List<String> findDistinctFuelTypes();

    @Query("""
            SELECT DISTINCT v.transmissionType
            FROM Vehicle v
            WHERE v.transmissionType IS NOT NULL
            ORDER BY v.transmissionType
            """)
    List<String> findDistinctTransmissionTypes();
}
