package com.dap.calculation.repository;

import com.dap.calculation.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CityRepository extends JpaRepository<City, Long> {

    List<City> findByStateCode(String stateCode);

    boolean existsByCityIdAndStateCode(Long cityId, String stateCode);
}