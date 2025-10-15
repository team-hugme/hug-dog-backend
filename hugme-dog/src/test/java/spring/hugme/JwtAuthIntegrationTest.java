package spring.hugme;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.LocalDate;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import spring.hugme.domain.auth.dto.response.LoginResponse;
import spring.hugme.domain.auth.service.AuthService;
import spring.hugme.domain.user.entity.Member;
import spring.hugme.domain.user.repository.UserRepository;
import spring.hugme.global.error.exceptions.AuthApiException;
import spring.hugme.global.response.ResponseCode;
import spring.hugme.infra.jwt.JwtProvider;
import spring.hugme.infra.redis.RedisService;

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

    private Member testUser;
    private static final String TEST_USERID = "testUser";
    private static final String TEST_PASSWORD = "password123";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        redisService.deleteRefreshToken(TEST_USERID);

        testUser = Member.builder()
            .userId(TEST_USERID)
            .password(passwordEncoder.encode(TEST_PASSWORD))
            .name("테스트유저")
            .birthday(LocalDate.of(2000, 1, 1))
            .phone("010-1234-5678")
            .build();

        userRepository.saveAndFlush(testUser);
        jwtProvider.generateAndStoreKey(TEST_USERID);
    }

    @BeforeEach
    void setUpSecurityContext() {
        UsernamePasswordAuthenticationToken auth =
            new UsernamePasswordAuthenticationToken(TEST_USERID, null, null);
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        try {
            redisService.deleteRefreshToken(TEST_USERID);
        } catch (Exception ignored) {
        }
    }

    @Test
    @DisplayName("로그인 성공 - JWT 토큰 발급")
    void testLoginSuccess() {
        LoginResponse response = authService.login(TEST_USERID, TEST_PASSWORD);

        assertThat(response).isNotNull();
        assertThat(response.getAccessToken()).isNotNull().isNotEmpty();
        assertThat(response.getRefreshToken()).isNotNull().isNotEmpty();
        assertThat(redisService.getRefreshToken(TEST_USERID))
            .isEqualTo(response.getRefreshToken());
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
        String userId = jwtProvider.validateAccessToken(response.getAccessToken());
        assertThat(userId).isEqualTo(TEST_USERID);
    }

    @Test
    @DisplayName("Refresh Token으로 토큰 재발급")
    void testReissueTokens() throws InterruptedException {
        LoginResponse loginResponse = authService.login(TEST_USERID, TEST_PASSWORD);
        String oldRefreshToken = loginResponse.getRefreshToken();
        String oldAccessToken = loginResponse.getAccessToken();

        Thread.sleep(1000); // iat 차이 보장

        LoginResponse reissued = authService.reissueRefreshTokens(TEST_USERID, oldRefreshToken);

        assertThat(reissued.getAccessToken()).isNotEqualTo(oldAccessToken);
        assertThat(reissued.getRefreshToken()).isNotEqualTo(oldRefreshToken);

        String userId = jwtProvider.validateAccessToken(reissued.getAccessToken());
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
    @DisplayName("userId를 알 때 Refresh Token 검증")
    void testValidateRefreshTokenWithUserId() {
        LoginResponse response = authService.login(TEST_USERID, TEST_PASSWORD);
        String userId = jwtProvider.validateRefreshToken(TEST_USERID, response.getRefreshToken());
        assertThat(userId).isEqualTo(TEST_USERID);
    }

    @Test
    @DisplayName("로그아웃 성공 - Refresh Token 삭제")
    void testLogoutSuccess() {
        authService.login(TEST_USERID, TEST_PASSWORD);
        authService.logout(TEST_USERID);

        String token = redisService.getRefreshToken(TEST_USERID);
        assertThat(token).isNull();
    }

    @Test
    @DisplayName("로그아웃 실패 - MISMATCH_TOKEN")
    void testLogoutMismatch() {
        authService.login(TEST_USERID, TEST_PASSWORD);

        String wrongUserId = "otherUser";

        AuthApiException ex = assertThrows(AuthApiException.class,
            () -> authService.logout(wrongUserId));

        assertThat(ex.getCode()).isEqualTo(ResponseCode.MISMATCH_TOKEN);
    }
}