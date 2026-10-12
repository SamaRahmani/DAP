package com.dap.calculation.repository;

import com.dap.calculation.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}