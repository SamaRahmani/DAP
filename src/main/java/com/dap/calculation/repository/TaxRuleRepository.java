package com.dap.calculation.repository;

import com.dap.calculation.entity.TaxRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxRuleRepository
        extends JpaRepository<TaxRule, Long> {
    List<TaxRule> findByStateCode(String stateCode);
}