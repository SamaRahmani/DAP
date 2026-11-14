
package com.dap.calculation.auth.jwt;

import java.time.Instant;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.*;

import org.springframework.stereotype.Service;

import com.dap.calculation.auth.entity.User;
import com.dap.calculation.auth.entity.Role;

@Service
public class JwtService {

    private final JwtEncoder jwtEncoder;

    @Value("${jwt.access-token-expiration:900}")
    private long accessTokenExpiration;

    public JwtService(JwtEncoder jwtEncoder) {
        this.jwtEncoder = jwtEncoder;
    }

    public String generateAccessToken(User user, String sessionId) {

        Instant now = Instant.now();

        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        JwtClaimsSet claims = JwtClaimsSet.builder()
                .issuer("dap-api")
                .subject(user.getId().toString())
                .issuedAt(now)
                .expiresAt(now.plus(Duration.ofSeconds(accessTokenExpiration)))
                .claim("email", user.getEmail())
                .claim("roles", roles)
                .claim("sid", sessionId)
                .build();

        JwsHeader header = JwsHeader.with(MacAlgorithm.HS256).build();

        JwtEncoderParameters parameters =
                JwtEncoderParameters.from(header, claims);

        return jwtEncoder.encode(parameters).getTokenValue();
    }
}
