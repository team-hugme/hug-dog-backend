package spring.hugme.security.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import spring.hugme.infra.error.exceptions.AuthApiException;
import spring.hugme.infra.response.ResponseCode;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration ACCESS_EXPIRE = Duration.ofMinutes(15);
    private static final Duration REFRESH_EXPIRE = Duration.ofDays(7);

    public void generateAndStoreKey(Long userId) {
        SecretKey key = Jwts.SIG.HS256.key().build();
        redisTemplate.opsForValue().set("JWT_KEY:" + userId, Encoders.BASE64.encode(key.getEncoded()));
    }

    private SecretKey getKey(Long userId) {
        String base64Key = redisTemplate.opsForValue().get("JWT_KEY:" + userId);
        if (base64Key == null) throw new AuthApiException(ResponseCode.INVALID_TOKEN);
        byte[] decoded = Decoders.BASE64.decode(base64Key);
        return Keys.hmacShaKeyFor(decoded);
    }

    public String generateAccessToken(Long userId) {
        return generateToken(userId, ACCESS_EXPIRE);
    }

    public String generateRefreshToken(Long userId) {
        return generateToken(userId, REFRESH_EXPIRE);
    }

    private String generateToken(Long userId, Duration expireDuration) {
        SecretKey key = getKey(userId);
        Instant now = Instant.now();
        Instant expiry = now.plus(expireDuration);

        return Jwts.builder()
            .subject(String.valueOf(userId))
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .id(UUID.randomUUID().toString())
            .signWith(key, Jwts.SIG.HS256)
            .compact();
    }

    // userId를 알고 있을 때 사용 (RefreshToken 검증)
    public Long validateToken(Long userId, String token) {
        try {
            SecretKey key = getKey(userId);
            String userIdStr = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
            return Long.parseLong(userIdStr);
        } catch (ExpiredJwtException e) {
            throw new AuthApiException(ResponseCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new AuthApiException(ResponseCode.INVALID_TOKEN);
        }
    }

    // userId를 모를 때 사용 (AccessToken 검증 - Filter 등에서)
    public Long validateToken(String token) throws AuthApiException {
        try {
            // 1. JWT Payload에서 userId 추출 (서명 검증 전)
            Long userId = extractUserIdFromToken(token);

            // 2. userId로 키를 가져와서 실제 서명 검증
            SecretKey key = getKey(userId);
            Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);

            return userId;
        } catch (ExpiredJwtException e) {
            throw new AuthApiException(ResponseCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new AuthApiException(ResponseCode.INVALID_TOKEN);
        }
    }

    // JWT Payload에서 userId 추출 (서명 검증 없이)
    private Long extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new AuthApiException(ResponseCode.INVALID_TOKEN);
            }

            // Payload 부분 디코딩
            byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
            String payload = new String(decodedBytes);

            // JSON에서 sub(userId) 추출
            JsonNode node = objectMapper.readTree(payload);
            String userId = node.get("sub").asText();

            return Long.parseLong(userId);
        } catch (Exception e) {
            throw new AuthApiException(ResponseCode.INVALID_TOKEN);
        }
    }
}