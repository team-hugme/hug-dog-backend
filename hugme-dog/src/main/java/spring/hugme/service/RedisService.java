package spring.hugme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisService {
    private final StringRedisTemplate redisTemplate;

    // userId -> refreshToken 저장
    public void saveRefreshToken(Long userId, String refreshToken) {
        redisTemplate.opsForValue().set("auth:refresh:" + userId, refreshToken, 7, TimeUnit.DAYS);
    }

    public String getRefreshToken(Long userId) {
        return redisTemplate.opsForValue().get("auth:refresh:" + userId);
    }

    public void deleteRefreshToken(Long userId) {
        redisTemplate.delete("auth:refresh:" + userId);
    }
}
