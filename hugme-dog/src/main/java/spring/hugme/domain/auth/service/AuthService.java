package spring.hugme.domain.auth.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.hugme.domain.auth.dto.request.SignUpRequest.SignUp;
import spring.hugme.domain.auth.dto.response.LoginResponse;
import spring.hugme.domain.user.entity.Member;
import spring.hugme.domain.user.repository.UserRepository;
import spring.hugme.global.error.exceptions.AuthApiException;
import spring.hugme.global.response.ResponseCode;
import spring.hugme.infra.jwt.JwtProvider;
import spring.hugme.infra.redis.RedisService;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;

    // -------------------------------
    // 회원가입
    // -------------------------------
    public Map<String, String> signup(SignUp dto) {
        if (userRepository.existsByUserId(dto.getUserId()))
            throw new AuthApiException(ResponseCode.CONFLICT_EXIST_USER);
        if (userRepository.existsByEmail(dto.getEmail()))
            throw new AuthApiException(ResponseCode.CONFLICT_EXIST_EMAIL);

        Member user = Member.builder()
            .id(dto.getId())
            .userId(dto.getUserId())
            .password(passwordEncoder.encode(dto.getPassword()))
            .email(dto.getEmail())
            .name(dto.getName())
            .birthday(dto.getBirthday())
            .phone(dto.getPhone())
            .build();

        userRepository.save(user);
        return Map.of("userId", dto.getUserId());
    }

    // -------------------------------
    // 로그인 (Access + Refresh 발급)
    // -------------------------------
    public LoginResponse login(String userId, String password) {
        Member user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new AuthApiException(ResponseCode.NOT_FOUND_USER));

        if (!user.checkPassword(passwordEncoder, password))
            throw new AuthApiException(ResponseCode.BAD_CREDENTIAL);

        // 사용자별 서명 키 생성
        jwtProvider.generateAndStoreKey(user.getUserId());

        // 토큰 생성
        String accessToken = jwtProvider.generateAccessToken(user.getUserId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUserId());

        // Redis에 Refresh Token 저장
        redisService.saveRefreshToken(user.getUserId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken);
    }

    // -------------------------------
    // Access Token 검증 (필터에서 호출)
    // -------------------------------
    public String validateAccessToken(String token) {
        // JWT 서명 + 만료 검증 후 userId 반환
        return jwtProvider.validateAccessToken(token);
    }

    // -------------------------------
    // Access Token 재발급 (Refresh Token 필요 없음)
    // -------------------------------
    public String reissueAccessToken(String userId) {
        // Redis에서 Refresh Token 존재 확인
        String refreshToken = redisService.getRefreshToken(userId);
        if (refreshToken == null) {
            throw new AuthApiException(ResponseCode.REFRESH_TOKEN_EXPIRED);
        }

        // 새 Access Token 생성
        return jwtProvider.generateAccessToken(userId);
    }

    // -------------------------------
    // Refresh Token 검증
    // -------------------------------
    public void validateRefreshToken(String userId, String refreshToken) {
        String savedToken = redisService.getRefreshToken(userId);
        if (savedToken == null || !savedToken.equals(refreshToken))
            throw new AuthApiException(ResponseCode.REFRESH_TOKEN_EXPIRED);

        // JWT 서명 검증
        jwtProvider.validateRefreshToken(userId, refreshToken);
    }

    // -------------------------------
    // Refresh Token 재발급 (Access + Refresh)
    // -------------------------------
    public LoginResponse reissueRefreshTokens(String userId, String refreshToken) {
        // 1. JWT 서명 검증
        String validatedUserId = jwtProvider.validateRefreshToken(userId, refreshToken);

        // 2. userId 일치 확인
        if (!validatedUserId.equals(userId))
            throw new AuthApiException(ResponseCode.INVALID_TOKEN);

        // 3. Redis 검증
        validateRefreshToken(userId, refreshToken);

        // 4. 새 토큰 생성
        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        // 5. Redis 업데이트
        redisService.saveRefreshToken(userId, newRefreshToken);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    // -------------------------------
    // 로그아웃
    // -------------------------------

    public void logout(String userId) {
        String authenticatedUserId = (String) SecurityContextHolder.getContext()
            .getAuthentication()
            .getPrincipal();

        log.info("로그아웃 요청 - RequestUserId: {}, TokenUserId: {}", userId, authenticatedUserId);

        // 2. 요청의 userId와 토큰의 userId 비교
        if (!userId.equals(authenticatedUserId)) {
            log.warn("토큰 주체와 요청된 사용자 불일치");
            throw new AuthApiException(ResponseCode.MISMATCH_TOKEN);
        }

        // 3. (선택) RefreshToken 등 삭제 처리
        redisService.deleteRefreshToken(userId);
        log.info("로그아웃 완료: {}", userId);
    }
}

