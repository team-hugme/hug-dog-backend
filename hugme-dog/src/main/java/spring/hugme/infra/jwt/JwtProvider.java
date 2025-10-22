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

    /**  1. 사용자별 서명 키를 생성하고 Redis에 저장 */
    public void generateAndStoreKey(String userId) {
        SecretKey key = Jwts.SIG.HS256.key().build();
        redisTemplate.opsForValue()
            .set("JWT_KEY:" + userId, Encoders.BASE64.encode(key.getEncoded()));
    }

    /**  2. Redis에서 해당 사용자의 서명 키 조회 */
    private SecretKey getKey(String userId) {
        String base64Key = redisTemplate.opsForValue().get("JWT_KEY:" + userId);
        if (base64Key == null) throw new AuthApiException(ResponseCode.INVALID_TOKEN);
        byte[] decoded = Decoders.BASE64.decode(base64Key);
        return Keys.hmacShaKeyFor(decoded);
    }

    /**  3. AccessToken / RefreshToken 생성 */
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

    /**  4. userId를 알고 있을 때 (RefreshToken 검증용) */
    public String validateToken(String userId, String token) {
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

    /**  5. userId를 모를 때 (AccessToken 검증용 - 필터 등) */
    public String validateToken(String token) {
        try {
            //  토큰에서 userId 추출
            String userId = extractUserIdFromToken(token);

            // userId 기반으로 키 가져와 검증
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

    /**  6. JWT Payload에서 userId(subject) 추출 (서명 검증 없이) */
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
