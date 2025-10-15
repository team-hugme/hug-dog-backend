package spring.hugme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import spring.hugme.infra.error.exceptions.AuthApiException;
import spring.hugme.security.jwt.JwtProvider;
import spring.hugme.service.RedisService;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName("JWT Provider 테스트")
class JwtProviderTest {

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private RedisService redisService;

    private final Long testUserId = 12345L;

    @BeforeEach
    void setUp() {
        // JWT Key 생성 및 저장
        jwtProvider.generateAndStoreKey(testUserId);
    }

    @AfterEach
    void tearDown() {
        redisService.deleteRefreshToken(testUserId);
    }

    @Test
    @DisplayName("Access Token 생성")
    void testGenerateAccessToken() {
        // when
        String accessToken = jwtProvider.generateAccessToken(testUserId);

        // then
        assertThat(accessToken).isNotNull();
        assertThat(accessToken.split("\\.")).hasSize(3); // JWT 형식 확인
    }

    @Test
    @DisplayName("Refresh Token 생성")
    void testGenerateRefreshToken() {
        // when
        String refreshToken = jwtProvider.generateRefreshToken(testUserId);

        // then
        assertThat(refreshToken).isNotNull();
        assertThat(refreshToken.split("\\.")).hasSize(3);
    }

    @Test
    @DisplayName("Token에서 userId 추출 - userId를 모를 때")
    void testValidateTokenWithoutKnowingUserId() {
        // given
        String token = jwtProvider.generateAccessToken(testUserId);

        // when
        Long extractedUserId = jwtProvider.validateToken(token);

        // then
        assertThat(extractedUserId).isEqualTo(testUserId);
    }

    @Test
    @DisplayName("Token 검증 - userId를 알 때")
    void testValidateTokenWithUserId() {
        // given
        String token = jwtProvider.generateAccessToken(testUserId);

        // when
        Long extractedUserId = jwtProvider.validateToken(testUserId, token);

        // then
        assertThat(extractedUserId).isEqualTo(testUserId);
    }

    @Test
    @DisplayName("잘못된 Token 검증 실패")
    void testValidateInvalidToken() {
        // given
        String invalidToken = "invalid.jwt.token";

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateToken(invalidToken))
                .isInstanceOf(AuthApiException.class);
    }

    @Test
    @DisplayName("다른 사용자의 Key로 검증 실패")
    void testValidateTokenWithWrongKey() {
        // given
        Long anotherUserId = 99999L;
        jwtProvider.generateAndStoreKey(anotherUserId);
        String token = jwtProvider.generateAccessToken(testUserId);

        // when & then
        assertThatThrownBy(() -> jwtProvider.validateToken(anotherUserId, token))
                .isInstanceOf(AuthApiException.class);
    }
}