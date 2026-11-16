package com.dap.calculation.repository;

import com.dap.calculation.entity.Insurance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface InsuranceRepository extends JpaRepository<Insurance, String> {

    boolean existsByProviderNameIgnoreCase(String providerName);

    Optional<Insurance> findByProviderNameIgnoreCase(String providerName);
}