
package com.dap.calculation.config;

import com.dap.calculation.auth.security.SessionValidationFilter;
import com.dap.calculation.auth.service.RefreshTokenService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;
import jakarta.servlet.DispatcherType;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.config.Customizer;

@Configuration
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SessionValidationFilter sessionValidationFilter(
            RefreshTokenService refreshTokenService) {

        return new SessionValidationFilter(refreshTokenService);
    }

    @Bean
    public FilterRegistrationBean<SessionValidationFilter>
    sessionValidationFilterRegistration(
            SessionValidationFilter filter) {

        FilterRegistrationBean<SessionValidationFilter> registration =
                new FilterRegistrationBean<>(filter);

        registration.setEnabled(false);

        return registration;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http,
            SessionValidationFilter sessionValidationFilter)
            throws Exception {

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable())

                .formLogin(form -> form.disable())
                .httpBasic(basic -> basic.disable())

                .sessionManagement(session ->
                        session.sessionCreationPolicy(
                                SessionCreationPolicy.STATELESS
                        )
                )

                .authorizeHttpRequests(auth -> auth

                        // Allow internal error dispatch
                        .dispatcherTypeMatchers(DispatcherType.ERROR).permitAll()

                        // Public authentication endpoints
                        .requestMatchers(
                                "/api/auth/**",
                                "/api/calculate"
                        ).permitAll()

                        // Public DAP calculation API
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/v1/dap/calculate"
                        ).permitAll()

                        // Public read-only master data APIs
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/v1/locations/**",
                                "/api/v1/insurance/**",
                                "/api/v1/vehicles/**"
                        ).permitAll()

                        // Admin-only endpoints
                        .requestMatchers(
                                "/api/v1/admin/**",
                                "/api/admin/**"
                        ).hasRole("ADMIN")

                        // Everything else requires authentication
                        .anyRequest().authenticated()
                )

                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt ->
                                jwt.jwtAuthenticationConverter(
                                        jwtAuthenticationConverter()
                                )
                        )
                )

                .addFilterAfter(
                        sessionValidationFilter,
                        BearerTokenAuthenticationFilter.class
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtGrantedAuthoritiesConverter authoritiesConverter =
                new JwtGrantedAuthoritiesConverter();

        authoritiesConverter.setAuthoritiesClaimName("roles");
        authoritiesConverter.setAuthorityPrefix("");

        JwtAuthenticationConverter converter =
                new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(
                authoritiesConverter
        );

        return converter;
    }
}
