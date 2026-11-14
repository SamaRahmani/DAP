
package com.dap.calculation.auth.service;

import com.dap.calculation.auth.dto.SignupRequest;
import com.dap.calculation.auth.dto.LoginRequest;
import com.dap.calculation.auth.dto.LoginResponse;

import com.dap.calculation.auth.entity.Role;
import com.dap.calculation.auth.entity.User;

import com.dap.calculation.auth.repository.RoleRepository;
import com.dap.calculation.auth.repository.UserRepository;

import com.dap.calculation.auth.jwt.JwtService;

import com.dap.calculation.exception.DuplicateResourceException;
import com.dap.calculation.exception.InvalidCredentialsException;
import com.dap.calculation.exception.AccountDisabledException;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.dap.calculation.auth.dto.RefreshTokenRequest;
import com.dap.calculation.auth.entity.UserSession;



import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    // SIGNUP
    public void signup(SignupRequest request) {

        // Check duplicate email
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateResourceException(
                    "Email already registered");
        }

        // Fetch default USER role
        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() ->
                        new IllegalStateException("ROLE_USER not found"));

        // Create user
        User user = new User();

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        // Hash password using B Crypt
        user.setPasswordHash(
                passwordEncoder.encode(request.getPassword()));

        user.setEnabled(true);
        user.setRoles(Set.of(userRole));

        // Save user
        userRepository.save(user);
    }

    // LOGIN
    public LoginResponse login(LoginRequest request) {

        // 1. Find user
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid email or password"));

        // 2. Check account status
        if (!user.isEnabled()) {
            throw new AccountDisabledException(
                    "Account is disabled");
        }

        // 3. Verify password
        if (!passwordEncoder.matches(
                request.getPassword(),
                user.getPasswordHash())) {

            throw new InvalidCredentialsException(
                    "Invalid email or password");
        }

        // 4. Create Redis session
        UserSession session =
                refreshTokenService.createSession(user.getId());

        // 5. Generate JWT with session ID
        String accessToken =
                jwtService.generateAccessToken(
                        user,
                        session.getSessionId()
                );

        // 6. Generate refresh token for this session
        String refreshToken =
                refreshTokenService.createRefreshToken(
                        session.getSessionId()
                );

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    // REFRESH ACCESS TOKEN
    public LoginResponse refresh(RefreshTokenRequest request) {

        // 1. Consume and validate old refresh token
        UserSession session =
                refreshTokenService.consumeRefreshToken(
                        request.getRefreshToken()
                );

        if (session == null) {
            throw new InvalidCredentialsException(
                    "Invalid or expired refresh token");
        }

        // 2. Find user
        User user = userRepository.findById(session.getUserId())
                .orElseThrow(() ->
                        new InvalidCredentialsException(
                                "Invalid or expired refresh token"));

        // 3. Check account status
        if (!user.isEnabled()) {
            refreshTokenService.revokeSession(
                    session.getSessionId()
            );

            throw new AccountDisabledException(
                    "Account is disabled");
        }

        // 4. Generate new access token
        String accessToken =
                jwtService.generateAccessToken(
                        user,
                        session.getSessionId()
                );

        // 5. Generate new refresh token
        String refreshToken =
                refreshTokenService.createRefreshToken(
                        session.getSessionId()
                );

        return new LoginResponse(
                accessToken,
                refreshToken,
                "Bearer"
        );
    }

    // LOGOUT
    public void logout(RefreshTokenRequest request) {

        refreshTokenService.revokeRefreshToken(
                request.getRefreshToken()
        );
    }
}
