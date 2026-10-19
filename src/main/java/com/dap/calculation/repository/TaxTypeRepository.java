package com.dap.calculation.repository;

import com.dap.calculation.entity.TaxType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TaxTypeRepository
        extends JpaRepository<TaxType, Long> {

    Optional<TaxType> findByTaxCode(String taxCode);

    boolean existsByTaxCode(String taxCode);
}