package com.dap.calculation.repository;

import com.dap.calculation.entity.TaxRuleCondition;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxRuleConditionRepository
        extends JpaRepository<TaxRuleCondition, Long> {

    List<TaxRuleCondition> findByTaxRuleId(Long taxRuleId);

    void deleteByTaxRuleId(Long taxRuleId);
}