package com.dap.calculation.repository;

import com.dap.calculation.entity.State;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StateRepository extends JpaRepository<State, String> {
}