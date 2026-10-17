package com.dap.calculation.repository;

import com.dap.calculation.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InsuranceRepository extends JpaRepository<Insurance, String> {

    boolean existsByProviderNameIgnoreCase(String providerName);
}