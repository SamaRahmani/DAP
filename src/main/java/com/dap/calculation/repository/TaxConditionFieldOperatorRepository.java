
package com.dap.calculation.repository;

import com.dap.calculation.entity.TaxConditionFieldOperator;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxConditionFieldOperatorRepository
        extends JpaRepository<TaxConditionFieldOperator, Long> {

    List<TaxConditionFieldOperator> findByConditionFieldId(
            Long conditionFieldId
    );

    boolean existsByConditionFieldIdAndOperator(
            Long conditionFieldId,
            String operator
    );

    boolean existsByConditionFieldIdAndOperatorAndConditionFieldOperatorIdNot(
            Long conditionFieldId,
            String operator,
            Long conditionFieldOperatorId
    );

    boolean existsByConditionFieldOperatorIdAndConditionFieldId(
            Long conditionFieldOperatorId,
            Long conditionFieldId
    );
}
