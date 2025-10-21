package spring.hugme;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.hugme.global.error.exceptions.AuthApiException;
import spring.hugme.domain.auth.dto.LoginResponse;
import spring.hugme.domain.user.entity.UserEntity;
import spring.hugme.domain.user.repository.UserRepository;
import spring.hugme.infra.jwt.JwtProvider;
import spring.hugme.domain.auth.service.AuthService;
import spring.hugme.infra.redis.RedisService;

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

    private UserEntity testUser;
    private static final String TEST_USERID = "testUser";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        userRepository.deleteAll();
        redisService.deleteRefreshToken(TEST_USERID);

        // 테스트용 유저 생성
        testUser = UserEntity.builder()
            .userId(TEST_USERID)
            .password(passwordEncoder.encode(TEST_PASSWORD))
            .email(TEST_USERID + "@example.com")
            .name("테스트유저")
            .birthday(LocalDate.of(2000, 1, 1))
            .phone("010-1234-5678")
            .active(true)
            .build();

        userRepository.saveAndFlush(testUser);

        // JWT 키 생성
        jwtProvider.generateAndStoreKey(TEST_USERID);
    }

    @AfterEach
    void tearDown() {
        try {
            redisService.deleteRefreshToken(TEST_USERID);
        } catch (Exception e) {
            // 테스트 정리 중 예외 무시
        }
    }

    @Test
    @DisplayName("로그인 성공 - JWT 토큰 발급")
    void testLoginSuccess() {
        LoginResponse response = authService.login(TEST_USERID, TEST_PASSWORD);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull().isNotEmpty();
        assertThat(response.getRefreshToken()).isNotNull().isNotEmpty();

        String savedToken = redisService.getRefreshToken(TEST_USERID);
        assertThat(savedToken).isEqualTo(response.getRefreshToken());
    }

    @Test
    @DisplayName("로그인 실패 - 잘못된 비밀번호")
    void testLoginFailWithWrongPassword() {
        assertThatExceptionOfType(AuthApiException.class)
            .isThrownBy(() -> authService.login(TEST_USERID, "wrongPassword"));
    }

    @Test
    @DisplayName("Access Token 검증 성공")
    void testValidateAccessToken() {
        LoginResponse response = authService.login(TEST_USERID, TEST_PASSWORD);
        String userId = jwtProvider.validateToken(response.getAccessToken());

        assertThat(userId).isEqualTo(TEST_USERID);
    }

    @Test
    @DisplayName("Refresh Token으로 토큰 재발급")
    void testReissueTokens() throws InterruptedException {
        LoginResponse loginResponse = authService.login(TEST_USERID, TEST_PASSWORD);
        String oldRefreshToken = loginResponse.getRefreshToken();
        String oldAccessToken = loginResponse.getAccessToken();

        Thread.sleep(1000); // iat 차이 보장

        LoginResponse reissued = authService.reissueTokens(TEST_USERID, oldRefreshToken);

        assertThat(reissued.getAccessToken()).isNotEqualTo(oldAccessToken);
        assertThat(reissued.getRefreshToken()).isNotEqualTo(oldRefreshToken);

        String userId = jwtProvider.validateToken(reissued.getAccessToken());
        assertThat(userId).isEqualTo(TEST_USERID);
    }

    @Test
    @DisplayName("Refresh Token 검증 실패 - Redis에 없는 토큰")
    void testValidateRefreshTokenFailWithInvalidToken() {
        authService.login(TEST_USERID, TEST_PASSWORD);
        String fakeToken = "fake.refresh.token";

        assertThatExceptionOfType(AuthApiException.class)
            .isThrownBy(() -> authService.validateRefreshToken(TEST_USERID, fakeToken));
    }

    @Test
    @DisplayName("로그아웃 - Refresh Token 삭제")
    void testLogout() {
        authService.login(TEST_USERID, TEST_PASSWORD);
        authService.logout(TEST_USERID);

        String token = redisService.getRefreshToken(TEST_USERID);
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("userId를 알 때 Refresh Token 검증")
    void testValidateRefreshTokenWithUserId() {
        LoginResponse response = authService.login(TEST_USERID, TEST_PASSWORD);
        String userId = jwtProvider.validateToken(TEST_USERID, response.getRefreshToken());

        assertThat(userId).isEqualTo(TEST_USERID);
    }
}
