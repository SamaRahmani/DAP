package com.dap.calculation.repository;

import com.dap.calculation.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StateRepository extends JpaRepository<State, String> {


    Optional<State> findByStateNameIgnoreCase(String stateName);
}