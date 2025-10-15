package spring.hugme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.hugme.infra.error.exceptions.AuthApiException;
import spring.hugme.model.dto.LoginResponse;
import spring.hugme.model.entity.UserEntity;
import spring.hugme.repository.UserRepository;
import spring.hugme.security.id.Snowflake;
import spring.hugme.security.jwt.JwtProvider;
import spring.hugme.service.AuthService;
import spring.hugme.service.RedisService;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest
@DisplayName("JWT 인증 통합 테스트")
class JwtAuthIntegrationTest {

    @Autowired
    private AuthService authService;

    @Autowired
    private JwtProvider jwtProvider;

    @Autowired
    private RedisService redisService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private Snowflake snowflake;

    private UserEntity testUser;
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        // 기존 테스트 데이터 정리
        userRepository.deleteAll();

        long userId = snowflake.nextId();
        testUser = UserEntity.builder()
            .id(userId)
            .userId("testuser_" + userId)
            .password(passwordEncoder.encode(TEST_PASSWORD))
            .email("test_" + userId + "@example.com")
            .name("테스트유저")
            .birthday(LocalDate.of(2000, 1, 1))
            .phone("010-1234-5678")
            .isActive(true)
            .build();

        userRepository.saveAndFlush(testUser);

        // JWT 키 생성
        jwtProvider.generateAndStoreKey(testUser.getId());
    }

    @AfterEach
    void tearDown() {
        try {
            if (testUser != null && testUser.getId() != null) {
                redisService.deleteRefreshToken(testUser.getId());
                userRepository.deleteById(testUser.getId());
            }
        } catch (Exception e) {
            // 테스트 정리 중 발생한 예외 무시
        }
    }

    @Test
    @DisplayName("로그인 성공 - JWT 토큰 발급")
    void testLoginSuccess() {
        // when
        LoginResponse response = authService.login(testUser.getUserId(), TEST_PASSWORD);

        // then
        assertThat(response)
            .isNotNull();
        assertThat(response.getAccessToken())
            .isNotNull()
            .isNotEmpty();
        assertThat(response.getRefreshToken())
            .isNotNull()
            .isNotEmpty();

        String savedToken = redisService.getRefreshToken(testUser.getId());
        assertThat(savedToken)
            .isEqualTo(response.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void testLoginFailWithWrongPassword() {
        // given
        String userId = testUser.getUserId();
        String wrongPassword = "wrongPassword";

        // when & then
        assertThatExceptionOfType(AuthApiException.class)
            .isThrownBy(() -> authService.login(userId, wrongPassword));
    }

    @Test
    @DisplayName("Access Token 검증 성공")
    void testValidateAccessToken() {
        // given
        LoginResponse response = authService.login(testUser.getUserId(), TEST_PASSWORD);

        // when
        Long userId = jwtProvider.validateToken(response.getAccessToken());

        // then
        assertThat(userId)
            .isNotNull()
            .isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Refresh Token으로 토큰 재발급")
    void testReissueTokens() throws InterruptedException {
        // given
        LoginResponse loginResponse = authService.login(testUser.getUserId(), TEST_PASSWORD);
        String oldRefreshToken = loginResponse.getRefreshToken();
        String oldAccessToken = loginResponse.getAccessToken();

        // 시간 차이 보장 (JWT의 iat 클레임이 밀리초 단위)
        Thread.sleep(1000);

        // when
        LoginResponse reissued = authService.reissueTokens(testUser.getId(), oldRefreshToken);

        // then
        assertThat(reissued.getAccessToken())
            .isNotNull()
            .isNotEqualTo(oldAccessToken);

        assertThat(reissued.getRefreshToken())
            .isNotNull()
            .isNotEqualTo(oldRefreshToken);

        // 새 토큰 검증
        Long userId = jwtProvider.validateToken(reissued.getAccessToken());
        assertThat(userId)
            .isEqualTo(testUser.getId());
    }

    @Test
    @DisplayName("Refresh Token 검증 실패 - Redis에 없는 토큰")
    void testValidateRefreshTokenFailWithInvalidToken() {
        // given
        authService.login(testUser.getUserId(), TEST_PASSWORD);
        Long userId = testUser.getId();
        String fakeToken = "fake.refresh.token";

        // when & then
        assertThatExceptionOfType(AuthApiException.class)
            .isThrownBy(() -> authService.validateRefreshToken(userId, fakeToken));
    }

    @Test
    @DisplayName("로그아웃 - Refresh Token 삭제")
    void testLogout() {
        // given
        authService.login(testUser.getUserId(), TEST_PASSWORD);

        // when
        authService.logout(testUser.getId());

        // then
        String token = redisService.getRefreshToken(testUser.getId());
        assertThat(token)
            .isNull();
    }

    @Test
    @DisplayName("userId를 알 때 Refresh Token 검증")
    void testValidateRefreshTokenWithUserId() {
        // given
        LoginResponse response = authService.login(testUser.getUserId(), TEST_PASSWORD);

        // when
        Long userId = jwtProvider.validateToken(testUser.getId(), response.getRefreshToken());

        // then
        assertThat(userId)
            .isNotNull()
            .isEqualTo(testUser.getId());
    }
}