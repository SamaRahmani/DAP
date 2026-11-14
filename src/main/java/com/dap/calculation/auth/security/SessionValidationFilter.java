
package com.dap.calculation.auth.security;

import com.dap.calculation.auth.entity.UserSession;
import com.dap.calculation.auth.service.RefreshTokenService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.dao.DataAccessException;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class SessionValidationFilter extends OncePerRequestFilter {

    private final RefreshTokenService refreshTokenService;

    public SessionValidationFilter(
            RefreshTokenService refreshTokenService) {
        this.refreshTokenService = refreshTokenService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuth) {

            String sessionId = jwtAuth.getToken()
                    .getClaimAsString("sid");

            // Reject JWTs without a session ID.
            if (sessionId == null || sessionId.isBlank()) {
                rejectUnauthorized(response);
                return;
            }

            try {

                UserSession session =
                        refreshTokenService.validateAndTouchSession(sessionId);

                // Session expired, revoked or does not exist.
                if (session == null) {
                    rejectUnauthorized(response);
                    return;
                }

            } catch (DataAccessException ex) {

                // Redis is unavailable or a Redis data-access operation failed.
                SecurityContextHolder.clearContext();

                response.setStatus(
                        HttpServletResponse.SC_SERVICE_UNAVAILABLE
                );

                response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                response.setCharacterEncoding(
                        StandardCharsets.UTF_8.name()
                );

                response.getWriter().write(
                        "{\"status\":503,"
                                + "\"error\":\"Service Unavailable\","
                                + "\"message\":\"Authentication service temporarily unavailable\"}"
                );

                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private void rejectUnauthorized(HttpServletResponse response)
            throws IOException {

        SecurityContextHolder.clearContext();

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        response.getWriter().write(
                "{\"status\":401,"
                        + "\"error\":\"Unauthorized\","
                        + "\"message\":\"Session expired or revoked\"}"
        );
    }
}
