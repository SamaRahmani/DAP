package com.dap.calculation.auth.entity;

import java.time.Instant;

public class UserSession {

    private String sessionId;
    private Long userId;
    private Instant createdAt;
    private Instant lastActivityAt;
    private Instant absoluteExpiry;

    public UserSession() {
    }

    public UserSession(
            String sessionId,
            Long userId,
            Instant createdAt,
            Instant lastActivityAt,
            Instant absoluteExpiry) {

        this.sessionId = sessionId;
        this.userId = userId;
        this.createdAt = createdAt;
        this.lastActivityAt = lastActivityAt;
        this.absoluteExpiry = absoluteExpiry;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getLastActivityAt() {
        return lastActivityAt;
    }

    public void setLastActivityAt(Instant lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public Instant getAbsoluteExpiry() {
        return absoluteExpiry;
    }

    public void setAbsoluteExpiry(Instant absoluteExpiry) {
        this.absoluteExpiry = absoluteExpiry;
    }
}