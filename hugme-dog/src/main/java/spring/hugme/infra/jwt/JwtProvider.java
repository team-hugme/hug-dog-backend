package spring.hugme.infra.jwt;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import spring.hugme.global.error.exceptions.AuthApiException;
import spring.hugme.global.response.ResponseCode;

@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    private static final Duration ACCESS_EXPIRE = Duration.ofMinutes(15);
    private static final Duration REFRESH_EXPIRE = Duration.ofDays(7);

    public void generateAndStoreKey(String userId) {
        SecretKey key = Jwts.SIG.HS256.key().build();
        redisTemplate.opsForValue()
            .set("JWT_KEY:" + userId, Encoders.BASE64.encode(key.getEncoded()));
    }

    private SecretKey getKey(String userId) {
        String base64Key = redisTemplate.opsForValue().get("JWT_KEY:" + userId);
        if (base64Key == null) throw new AuthApiException(ResponseCode.INVALID_TOKEN);
        byte[] decoded = Decoders.BASE64.decode(base64Key);
        return Keys.hmacShaKeyFor(decoded);
    }

    public String generateAccessToken(String userId) {
        return generateToken(userId, ACCESS_EXPIRE);
    }

    public String generateRefreshToken(String userId) {
        return generateToken(userId, REFRESH_EXPIRE);
    }

    private String generateToken(String userId, Duration expireDuration) {
        SecretKey key = getKey(userId);
        Instant now = Instant.now();
        Instant expiry = now.plus(expireDuration);

        return Jwts.builder()
            .subject(userId)
            .issuedAt(Date.from(now))
            .expiration(Date.from(expiry))
            .id(UUID.randomUUID().toString())
            .signWith(key, Jwts.SIG.HS256)
            .compact();
    }

    public String validateRefreshToken(String userId, String token) {
        try {
            SecretKey key = getKey(userId);
            return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
        } catch (ExpiredJwtException e) {
            throw new AuthApiException(ResponseCode.TOKEN_EXPIRED);
        } catch (JwtException e) {
            throw new AuthApiException(ResponseCode.INVALID_TOKEN);
        }
    }

    public String validateAccessToken(String token) {
        try {
            String userId = extractUserIdFromToken(token);

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

    private String extractUserIdFromToken(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new AuthApiException(ResponseCode.INVALID_TOKEN);
            }

            byte[] decodedBytes = Base64.getUrlDecoder().decode(parts[1]);
            String payload = new String(decodedBytes);

            JsonNode node = objectMapper.readTree(payload);
            return node.get("sub").asText();
        } catch (Exception e) {
            throw new AuthApiException(ResponseCode.INVALID_TOKEN);
        }
    }
}
