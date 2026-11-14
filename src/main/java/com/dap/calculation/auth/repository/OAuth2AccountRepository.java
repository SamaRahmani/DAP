
package com.dap.calculation.auth.repository;

import com.dap.calculation.auth.entity.OAuth2Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OAuth2AccountRepository
        extends JpaRepository<OAuth2Account, Long> {

    Optional<OAuth2Account> findByProviderAndProviderUserId(
            String provider,
            String providerUserId
    );

    boolean existsByProviderAndProviderUserId(
            String provider,
            String providerUserId
    );
}
