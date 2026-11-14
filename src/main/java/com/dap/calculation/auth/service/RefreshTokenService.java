
package com.dap.calculation.auth.service;

import com.dap.calculation.auth.entity.UserSession;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Map;
import java.util.UUID;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;

@Service
public class RefreshTokenService {

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    private static final Duration REFRESH_TOKEN_TTL = Duration.ofDays(7);
    private static final Duration INACTIVITY_TIMEOUT = Duration.ofMinutes(30);
    private static final Duration ABSOLUTE_SESSION_TIMEOUT = Duration.ofHours(12);

    // A recently consumed token is tolerated briefly to handle overlapping requests.
    private static final Duration REFRESH_REUSE_GRACE_PERIOD =
            Duration.ofSeconds(30);

    private static final String SESSION_PREFIX = "session:";
    private static final String REFRESH_PREFIX = "refresh_token:";
    private static final String USED_REFRESH_PREFIX = "used_refresh_token:";
    private static final String SESSION_TOKENS_PREFIX =
            "session_refresh_tokens:";

    // Tracks all active sessions belonging to a user.
    private static final String USER_SESSIONS_PREFIX = "user_sessions:";

    private static final DefaultRedisScript<List> CONSUME_AND_VALIDATE_SCRIPT;
    private static final DefaultRedisScript<List> VALIDATE_SESSION_SCRIPT;
    private static final DefaultRedisScript<Long> REVOKE_SESSION_SCRIPT;
    private static final DefaultRedisScript<String> CREATE_REFRESH_TOKEN_SCRIPT;
    private static final DefaultRedisScript<String> DETECT_REFRESH_REUSE_SCRIPT;


    static {

        // SCRIPT 1: CONSUME AND VALIDATE REFRESH TOKEN

        CONSUME_AND_VALIDATE_SCRIPT = new DefaultRedisScript<>();

        CONSUME_AND_VALIDATE_SCRIPT.setScriptText(
                "local refreshKey = KEYS[1] " +
                        "local tokenSetKey = KEYS[2] " +
                        "local sessionKey = KEYS[3] " +
                        "local usedKey = KEYS[4] " +

                        "local storedSessionId = redis.call('GET', refreshKey) " +

                        "if not storedSessionId or storedSessionId ~= ARGV[1] then " +
                        "    return {'INVALID'} " +
                        "end " +

                        "local data = redis.call('HMGET', sessionKey, " +
                        "'userId', 'createdAt', 'lastActivityAt', 'absoluteExpiry') " +

                        "local userId = tonumber(data[1]) " +
                        "local createdAt = tonumber(data[2]) " +
                        "local lastActivityAt = tonumber(data[3]) " +
                        "local absoluteExpiry = tonumber(data[4]) " +

                        "if not userId or not createdAt or " +
                        "not lastActivityAt or not absoluteExpiry then " +

                        "    local tokens = redis.call('SMEMBERS', tokenSetKey) " +
                        "    for i = 1, #tokens do " +
                        "        redis.call('DEL', tokens[i]) " +
                        "    end " +

                        "    redis.call('DEL', refreshKey, tokenSetKey, sessionKey) " +
                        "    return {'INVALID'} " +
                        "end " +

                        "local time = redis.call('TIME') " +
                        "local now = tonumber(time[1]) * 1000 + " +
                        "math.floor(tonumber(time[2]) / 1000) " +

                        "local inactivityTimeout = tonumber(ARGV[2]) " +

                        "if now >= absoluteExpiry or " +
                        "now - lastActivityAt >= inactivityTimeout then " +

                        "    local tokens = redis.call('SMEMBERS', tokenSetKey) " +
                        "    for i = 1, #tokens do " +
                        "        redis.call('DEL', tokens[i]) " +
                        "    end " +

                        "    redis.call('DEL', refreshKey, tokenSetKey, sessionKey) " +
                        "    return {'EXPIRED'} " +
                        "end " +

                        // Retain a record of the consumed token until session expiry.
                        "local remaining = absoluteExpiry - now " +
                        "local usedRecord = storedSessionId .. '|' .. tostring(now) " +

                        "redis.call('SET', usedKey, usedRecord, 'PX', remaining) " +

                        // Consume the original refresh token.
                        "redis.call('DEL', refreshKey) " +
                        "redis.call('SREM', tokenSetKey, refreshKey) " +

                        // Update session activity.
                        "redis.call('HSET', sessionKey, " +
                        "'lastActivityAt', tostring(now)) " +

                        "local ttl = math.min(inactivityTimeout, remaining) " +
                        "redis.call('PEXPIRE', sessionKey, ttl) " +

                        "return {'OK', data[1], data[2], " +
                        "tostring(now), data[4]}"
        );

        CONSUME_AND_VALIDATE_SCRIPT.setResultType(List.class);


        // SCRIPT 2: VALIDATE AND TOUCH SESSION

        VALIDATE_SESSION_SCRIPT = new DefaultRedisScript<>();

        VALIDATE_SESSION_SCRIPT.setScriptText(
                "local sessionKey = KEYS[1] " +
                        "local tokenSetKey = KEYS[2] " +

                        "local data = redis.call('HMGET', sessionKey, " +
                        "'userId', 'createdAt', 'lastActivityAt', 'absoluteExpiry') " +

                        "if not data[1] then return {'INVALID'} end " +

                        "local userId = tonumber(data[1]) " +
                        "local createdAt = tonumber(data[2]) " +
                        "local lastActivityAt = tonumber(data[3]) " +
                        "local absoluteExpiry = tonumber(data[4]) " +

                        "if not userId or not createdAt or " +
                        "not lastActivityAt or not absoluteExpiry then " +

                        "    local tokens = redis.call('SMEMBERS', tokenSetKey) " +
                        "    for i = 1, #tokens do " +
                        "        redis.call('DEL', tokens[i]) " +
                        "    end " +

                        "    redis.call('DEL', tokenSetKey, sessionKey) " +
                        "    return {'INVALID'} " +
                        "end " +

                        "local time = redis.call('TIME') " +
                        "local now = tonumber(time[1]) * 1000 + " +
                        "math.floor(tonumber(time[2]) / 1000) " +

                        "local inactivityTimeout = tonumber(ARGV[1]) " +

                        "if now >= absoluteExpiry or " +
                        "now - lastActivityAt >= inactivityTimeout then " +

                        "    local tokens = redis.call('SMEMBERS', tokenSetKey) " +
                        "    for i = 1, #tokens do " +
                        "        redis.call('DEL', tokens[i]) " +
                        "    end " +

                        "    redis.call('DEL', tokenSetKey, sessionKey) " +
                        "    return {'EXPIRED'} " +
                        "end " +

                        "redis.call('HSET', sessionKey, " +
                        "'lastActivityAt', tostring(now)) " +

                        "local remaining = absoluteExpiry - now " +
                        "local ttl = math.min(inactivityTimeout, remaining) " +

                        "redis.call('PEXPIRE', sessionKey, ttl) " +

                        "return {'OK', data[1], data[2], " +
                        "tostring(now), data[4]}"
        );

        VALIDATE_SESSION_SCRIPT.setResultType(List.class);


        // SCRIPT 3: ATOMIC SESSION REVOCATION

        REVOKE_SESSION_SCRIPT = new DefaultRedisScript<>();

        REVOKE_SESSION_SCRIPT.setScriptText(
                "local tokenSetKey = KEYS[1] " +
                        "local sessionKey = KEYS[2] " +
                        "local userSessionsKey = KEYS[3] " +

                        "local tokens = redis.call('SMEMBERS', tokenSetKey) " +

                        "for i = 1, #tokens do " +
                        "    redis.call('DEL', tokens[i]) " +
                        "end " +

                        // Remove this session from the user's session index.
                        "redis.call('SREM', userSessionsKey, ARGV[1]) " +

                        "redis.call('DEL', tokenSetKey, sessionKey) " +

                        "return 1"
        );

        REVOKE_SESSION_SCRIPT.setResultType(Long.class);


        // SCRIPT 4: ATOMIC REFRESH-TOKEN CREATION

        CREATE_REFRESH_TOKEN_SCRIPT = new DefaultRedisScript<>();

        CREATE_REFRESH_TOKEN_SCRIPT.setScriptText(
                "local sessionKey = KEYS[1] " +
                        "local tokenSetKey = KEYS[2] " +
                        "local refreshKey = KEYS[3] " +

                        "local data = redis.call('HMGET', sessionKey, " +
                        "'userId', 'lastActivityAt', 'absoluteExpiry') " +

                        "local userId = tonumber(data[1]) " +
                        "local lastActivityAt = tonumber(data[2]) " +
                        "local absoluteExpiry = tonumber(data[3]) " +

                        "if not userId or not lastActivityAt or " +
                        "not absoluteExpiry then return 'INVALID' end " +

                        "local time = redis.call('TIME') " +
                        "local now = tonumber(time[1]) * 1000 + " +
                        "math.floor(tonumber(time[2]) / 1000) " +

                        "local refreshTokenTtl = tonumber(ARGV[2]) " +
                        "local inactivityTimeout = tonumber(ARGV[3]) " +

                        "if now >= absoluteExpiry or " +
                        "now - lastActivityAt >= inactivityTimeout then " +

                        "    local tokens = redis.call('SMEMBERS', tokenSetKey) " +
                        "    for i = 1, #tokens do " +
                        "        redis.call('DEL', tokens[i]) " +
                        "    end " +

                        "    redis.call('DEL', tokenSetKey, sessionKey) " +
                        "    return 'EXPIRED' " +
                        "end " +

                        "local remaining = absoluteExpiry - now " +
                        "local tokenTtl = math.min(refreshTokenTtl, remaining) " +
                        "local sessionTtl = math.min(inactivityTimeout, remaining) " +

                        "redis.call('SET', refreshKey, ARGV[1], 'PX', tokenTtl) " +
                        "redis.call('SADD', tokenSetKey, refreshKey) " +
                        "redis.call('PEXPIRE', tokenSetKey, remaining) " +

                        "redis.call('HSET', sessionKey, " +
                        "'lastActivityAt', tostring(now)) " +

                        "redis.call('PEXPIRE', sessionKey, sessionTtl) " +

                        "return 'OK'"
        );

        CREATE_REFRESH_TOKEN_SCRIPT.setResultType(String.class);


        // SCRIPT 5: DETECT REUSE OF A CONSUMED REFRESH TOKEN

        DETECT_REFRESH_REUSE_SCRIPT = new DefaultRedisScript<>();

        DETECT_REFRESH_REUSE_SCRIPT.setScriptText(
                "local usedKey = KEYS[1] " +
                        "local tokenSetKey = KEYS[2] " +
                        "local sessionKey = KEYS[3] " +

                        "local record = redis.call('GET', usedKey) " +

                        "if not record or record ~= ARGV[1] then " +
                        "    return 'INVALID' " +
                        "end " +

                        "local separator = string.find(record, '|', 1, true) " +

                        "if not separator then return 'INVALID' end " +

                        "local consumedAt = tonumber(string.sub(record, separator + 1)) " +

                        "if not consumedAt then return 'INVALID' end " +

                        "local time = redis.call('TIME') " +
                        "local now = tonumber(time[1]) * 1000 + " +
                        "math.floor(tonumber(time[2]) / 1000) " +

                        "local gracePeriod = tonumber(ARGV[2]) " +

                        // Treat recent duplicate requests as concurrent refresh attempts.
                        "if now - consumedAt <= gracePeriod then " +
                        "    return 'RECENT' " +
                        "end " +

                        // Reuse outside the grace period revokes the session.
                        "local tokens = redis.call('SMEMBERS', tokenSetKey) " +

                        "for i = 1, #tokens do " +
                        "    redis.call('DEL', tokens[i]) " +
                        "end " +

                        "redis.call('DEL', tokenSetKey, sessionKey) " +

                        "return 'REUSED'"
        );

        DETECT_REFRESH_REUSE_SCRIPT.setResultType(String.class);
    }


    public RefreshTokenService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }


    // 1. CREATE A NEW USER SESSION

    public UserSession createSession(Long userId) {

        Instant now = Instant.now();

        String sessionId = UUID.randomUUID().toString();

        UserSession session = new UserSession(
                sessionId,
                userId,
                now,
                now,
                now.plus(ABSOLUTE_SESSION_TIMEOUT)
        );

        String sessionKey = SESSION_PREFIX + sessionId;

        Map<String, String> sessionData = Map.of(
                "sessionId", sessionId,
                "userId", userId.toString(),
                "createdAt", String.valueOf(now.toEpochMilli()),
                "lastActivityAt", String.valueOf(now.toEpochMilli()),
                "absoluteExpiry",
                String.valueOf(session.getAbsoluteExpiry().toEpochMilli())
        );

        redisTemplate.opsForHash().putAll(sessionKey, sessionData);
        redisTemplate.expire(sessionKey, INACTIVITY_TIMEOUT);

        // Register this session against its user.
        String userSessionsKey = USER_SESSIONS_PREFIX + userId;

        redisTemplate.opsForSet().add(userSessionsKey, sessionId);

        // The index must not outlive the session's absolute expiry.
        redisTemplate.expire(userSessionsKey, ABSOLUTE_SESSION_TIMEOUT);

        return session;
    }


    // 2. GENERATE AND STORE A REFRESH TOKEN ATOMICALLY

    public String createRefreshToken(String sessionId) {

        if (sessionId == null || sessionId.isBlank()) {
            throw new IllegalArgumentException("Session ID cannot be empty");
        }

        byte[] randomBytes = new byte[32];
        secureRandom.nextBytes(randomBytes);

        String refreshToken = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(randomBytes);

        String tokenHash = hashToken(refreshToken);

        String refreshKey = REFRESH_PREFIX + tokenHash;
        String sessionKey = SESSION_PREFIX + sessionId;
        String tokenSetKey = SESSION_TOKENS_PREFIX + sessionId;

        String result = redisTemplate.execute(
                CREATE_REFRESH_TOKEN_SCRIPT,
                List.of(sessionKey, tokenSetKey, refreshKey),
                sessionId,
                String.valueOf(REFRESH_TOKEN_TTL.toMillis()),
                String.valueOf(INACTIVITY_TIMEOUT.toMillis())
        );

        if (!"OK".equals(result)) {
            throw new IllegalStateException(
                    "Cannot create refresh token: session is invalid or expired"
            );
        }

        return refreshToken;
    }


    // 3. CONSUME AND VALIDATE A REFRESH TOKEN

    public UserSession consumeRefreshToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return null;
        }

        String tokenHash = hashToken(refreshToken);

        String refreshKey = REFRESH_PREFIX + tokenHash;
        String usedKey = USED_REFRESH_PREFIX + tokenHash;

        String sessionId = redisTemplate.opsForValue().get(refreshKey);

        // Token may already have been consumed.
        if (sessionId == null) {
            handleRefreshTokenReuse(usedKey);
            return null;
        }

        String tokenSetKey = SESSION_TOKENS_PREFIX + sessionId;
        String sessionKey = SESSION_PREFIX + sessionId;

        List<?> result = redisTemplate.execute(
                CONSUME_AND_VALIDATE_SCRIPT,
                List.of(refreshKey, tokenSetKey, sessionKey, usedKey),
                sessionId,
                String.valueOf(INACTIVITY_TIMEOUT.toMillis())
        );

        if (result == null || result.isEmpty()) {
            return null;
        }

        String status = result.get(0).toString();

        if (!"OK".equals(status)) {
            // Covers the race where another request consumed the token first.
            handleRefreshTokenReuse(usedKey);
            return null;
        }

        Long userId = Long.valueOf(result.get(1).toString());

        Instant createdAt = Instant.ofEpochMilli(
                Long.parseLong(result.get(2).toString())
        );

        Instant lastActivityAt = Instant.ofEpochMilli(
                Long.parseLong(result.get(3).toString())
        );

        Instant absoluteExpiry = Instant.ofEpochMilli(
                Long.parseLong(result.get(4).toString())
        );

        return new UserSession(
                sessionId,
                userId,
                createdAt,
                lastActivityAt,
                absoluteExpiry
        );
    }


    // 4. CHECK WHETHER A CONSUMED TOKEN HAS BEEN REUSED

    private void handleRefreshTokenReuse(String usedKey) {

        String usedRecord = redisTemplate.opsForValue().get(usedKey);

        if (usedRecord == null) {
            return;
        }

        int separator = usedRecord.indexOf('|');

        if (separator <= 0) {
            return;
        }

        String sessionId = usedRecord.substring(0, separator);

        String tokenSetKey = SESSION_TOKENS_PREFIX + sessionId;
        String sessionKey = SESSION_PREFIX + sessionId;

        redisTemplate.execute(
                DETECT_REFRESH_REUSE_SCRIPT,
                List.of(usedKey, tokenSetKey, sessionKey),
                usedRecord,
                String.valueOf(REFRESH_REUSE_GRACE_PERIOD.toMillis())
        );
    }


    // 5. VALIDATE AND UPDATE SESSION ACTIVITY

    public UserSession validateAndTouchSession(String sessionId) {

        if (sessionId == null || sessionId.isBlank()) {
            return null;
        }

        String sessionKey = SESSION_PREFIX + sessionId;
        String tokenSetKey = SESSION_TOKENS_PREFIX + sessionId;

        List<?> result = redisTemplate.execute(
                VALIDATE_SESSION_SCRIPT,
                List.of(sessionKey, tokenSetKey),
                String.valueOf(INACTIVITY_TIMEOUT.toMillis())
        );

        if (result == null || result.isEmpty()) {
            return null;
        }

        String status = result.get(0).toString();

        if (!"OK".equals(status)) {
            return null;
        }

        Long userId = Long.valueOf(result.get(1).toString());

        Instant createdAt = Instant.ofEpochMilli(
                Long.parseLong(result.get(2).toString())
        );

        Instant lastActivityAt = Instant.ofEpochMilli(
                Long.parseLong(result.get(3).toString())
        );

        Instant absoluteExpiry = Instant.ofEpochMilli(
                Long.parseLong(result.get(4).toString())
        );

        return new UserSession(
                sessionId,
                userId,
                createdAt,
                lastActivityAt,
                absoluteExpiry
        );
    }


    // 6. ATOMICALLY REVOKE AN ENTIRE SESSION

    public void revokeSession(String sessionId) {

        if (sessionId == null || sessionId.isBlank()) {
            return;
        }

        String tokenSetKey = SESSION_TOKENS_PREFIX + sessionId;
        String sessionKey = SESSION_PREFIX + sessionId;

        // Read the user ID before deleting the session hash.
        Object userId = redisTemplate.opsForHash()
                .get(sessionKey, "userId");

        String userSessionsKey = USER_SESSIONS_PREFIX
                + (userId != null ? userId.toString() : "unknown");

        redisTemplate.execute(
                REVOKE_SESSION_SCRIPT,
                List.of(tokenSetKey, sessionKey, userSessionsKey),
                sessionId
        );
    }


    // 7. REVOKE ALL SESSIONS BELONGING TO A USER

    public void revokeAllUserSessions(Long userId) {

        if (userId == null) {
            return;
        }

        String userSessionsKey = USER_SESSIONS_PREFIX + userId;

        Set<String> sessionIds = redisTemplate.opsForSet()
                .members(userSessionsKey);

        if (sessionIds == null || sessionIds.isEmpty()) {
            redisTemplate.delete(userSessionsKey);
            return;
        }

        // Copy the set so session revocation can safely modify the Redis index.
        List<String> sessionsToRevoke = new ArrayList<>(sessionIds);

        for (String sessionId : sessionsToRevoke) {
            revokeSession(sessionId);
        }

        // Remove any stale session IDs remaining in the index.
        redisTemplate.delete(userSessionsKey);
    }


    // 8. REVOKE USING A REFRESH TOKEN

    public void revokeRefreshToken(String refreshToken) {

        if (refreshToken == null || refreshToken.isBlank()) {
            return;
        }

        String tokenHash = hashToken(refreshToken);
        String refreshKey = REFRESH_PREFIX + tokenHash;

        String sessionId = redisTemplate.opsForValue().get(refreshKey);

        if (sessionId != null) {
            revokeSession(sessionId);
        }
    }


    // 9. HASH REFRESH TOKEN BEFORE REDIS STORAGE

    private String hashToken(String token) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(StandardCharsets.UTF_8)
            );

            return Base64.getUrlEncoder()
                    .withoutPadding()
                    .encodeToString(hash);

        } catch (Exception ex) {

            throw new IllegalStateException(
                    "Unable to hash refresh token",
                    ex
            );
        }
    }
}
