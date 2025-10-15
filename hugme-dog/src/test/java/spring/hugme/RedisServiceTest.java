package spring.hugme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.hugme.service.RedisService;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("Redis Service 테스트")
class RedisServiceTest {

    @Autowired
    private RedisService redisService;

    private final Long testUserId = 12345L;
    private final String testToken = "refreshToken123";

    @AfterEach
    void tearDown() {
        redisService.deleteRefreshToken(testUserId);
    }

    @Test
    @DisplayName("Refresh Token 저장 및 조회")
    void testSaveAndGetRefreshToken() {
        // given & when
        redisService.saveRefreshToken(testUserId, testToken);

        // then
        String tokenFromRedis = redisService.getRefreshToken(testUserId);
        assertThat(tokenFromRedis).isEqualTo(testToken);
    }

    @Test
    @DisplayName("Refresh Token 삭제")
    void testDeleteRefreshToken() {
        // given
        redisService.saveRefreshToken(testUserId, testToken);

        // when
        redisService.deleteRefreshToken(testUserId);

        // then
        String tokenFromRedis = redisService.getRefreshToken(testUserId);
        assertThat(tokenFromRedis).isNull();
    }

    @Test
    @DisplayName("존재하지 않는 Refresh Token 조회")
    void testGetNonExistentRefreshToken() {
        // when
        String tokenFromRedis = redisService.getRefreshToken(99999L);

        // then
        assertThat(tokenFromRedis).isNull();
    }
}