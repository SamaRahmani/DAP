
package com.dap.calculation.repository;

import com.dap.calculation.entity.TaxCalculationBase;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaxCalculationBaseRepository
        extends JpaRepository<TaxCalculationBase, Long> {

    Optional<TaxCalculationBase> findByBaseCode(String baseCode);

    Optional<TaxCalculationBase> findByCalculationBaseIdAndIsActiveTrue(
            Long calculationBaseId
    );

    List<TaxCalculationBase> findByIsActiveTrue();

    boolean existsByBaseCode(String baseCode);

    boolean existsByBaseCodeAndCalculationBaseIdNot(
            String baseCode,
            Long calculationBaseId
    );
}
