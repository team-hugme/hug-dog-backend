package spring.hugme.infra.redis;

import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class RedisService {

    private final StringRedisTemplate redisTemplate;

    private static final String REFRESH_TOKEN_PREFIX = "auth:refresh:";

    public void saveRefreshToken(String userId, String refreshToken) {
        redisTemplate.opsForValue()
            .set(buildKey(userId), refreshToken, 7, TimeUnit.DAYS);
    }

    public String getRefreshToken(String userId) {
        return redisTemplate.opsForValue().get(buildKey(userId));
    }

    public void deleteRefreshToken(String userId) {
        redisTemplate.delete(buildKey(userId));
    }

    private String buildKey(String userId) {
        return REFRESH_TOKEN_PREFIX + userId;
    }
}
