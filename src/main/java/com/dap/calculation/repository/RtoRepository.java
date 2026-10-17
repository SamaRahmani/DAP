package com.dap.calculation.repository;

import com.dap.calculation.entity.Rto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RtoRepository extends JpaRepository<Rto, String> {

    List<Rto> findByCityId(Long cityId);

    List<Rto> findByStateCode(String stateCode);

    boolean existsByCityId(Long cityId);
}