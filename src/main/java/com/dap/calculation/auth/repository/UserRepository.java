
package com.dap.calculation.auth.repository;

import com.dap.calculation.auth.entity.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
            String fullName,
            String email
    );

    @Query("""
            SELECT COUNT(DISTINCT u)
            FROM User u
            JOIN u.roles r
            WHERE u.enabled = true
            AND r.name = :roleName
            """)
    long countEnabledUsersByRoleName(
            @Param("roleName") String roleName
    );
}
