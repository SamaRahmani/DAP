package com.dap.calculation.repository;

import com.dap.calculation.entity.TaxRuleApplicability;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxRuleApplicabilityRepository
        extends JpaRepository<TaxRuleApplicability, Long> {

    List<TaxRuleApplicability> findByTaxRuleId(Long taxRuleId);

    List<TaxRuleApplicability> findByApplicabilityTypeAndApplicabilityCode(
            String applicabilityType,
            String applicabilityCode
    );

    void deleteByTaxRuleId(Long taxRuleId);
}