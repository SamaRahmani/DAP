
package com.dap.calculation.repository;

import com.dap.calculation.entity.TaxConditionField;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaxConditionFieldRepository
        extends JpaRepository<TaxConditionField, Long> {

    Optional<TaxConditionField> findByFieldCodeAndIsActiveTrue(
            String fieldCode
    );

    Optional<TaxConditionField> findByFieldCode(String fieldCode);

    List<TaxConditionField> findByIsActiveTrue();

    boolean existsByFieldCode(String fieldCode);

    boolean existsByFieldCodeAndConditionFieldIdNot(
            String fieldCode,
            Long conditionFieldId
    );
}
