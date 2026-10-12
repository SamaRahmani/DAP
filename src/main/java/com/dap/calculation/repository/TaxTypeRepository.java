package com.dap.calculation.repository;

import com.dap.calculation.entity.TaxType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TaxTypeRepository
        extends JpaRepository<TaxType, String> {
}