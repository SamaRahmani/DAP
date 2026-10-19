package com.dap.calculation.repository;

import com.dap.calculation.entity.TaxRule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxRuleRepository extends JpaRepository<TaxRule, Long> {

    List<TaxRule> findByTaxTypeId(Long taxTypeId);

    List<TaxRule> findByIsActiveTrue();

    List<TaxRule> findByTaxTypeIdAndIsActiveTrue(Long taxTypeId);
}