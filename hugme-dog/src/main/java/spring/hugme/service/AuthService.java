package spring.hugme.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import spring.hugme.infra.error.exceptions.AuthApiException;
import spring.hugme.infra.response.ResponseCode;
import spring.hugme.model.dto.LoginResponse;
import spring.hugme.model.dto.UserRequestDto.SignUp;
import spring.hugme.model.entity.UserEntity;
import spring.hugme.repository.UserRepository;
import spring.hugme.security.id.Snowflake;
import spring.hugme.security.jwt.JwtProvider;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;
    private final RedisService redisService;
    private final Snowflake snowflake;

    // 회원가입
    public Map<String, String> signup(SignUp dto) {
        if (userRepository.existsByUserId(dto.getUserId()))
            throw new AuthApiException(ResponseCode.CONFLICT_EXIST_USER);

        if (userRepository.existsByEmail(dto.getEmail()))
            throw new AuthApiException(ResponseCode.CONFLICT_EXIST_EMAIL);

        long newId = snowflake.nextId();
        UserEntity user = UserEntity.builder()
                .id(newId)
                .userId(dto.getUserId())
                .password(passwordEncoder.encode(dto.getPassword()))
                .email(dto.getEmail())
                .name(dto.getName())
                .birthday(dto.getBirthday())
                .phone(dto.getPhone())
                .isActive(true)
                .build();
        userRepository.save(user);

        return Map.of("userId", dto.getUserId());
    }

    // 로그인
    public LoginResponse login(String userId, String password) {
        UserEntity user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new AuthApiException(ResponseCode.NOT_FOUND_USER));

        if (!user.checkPassword(passwordEncoder, password)) {
            throw new AuthApiException(ResponseCode.BAD_CREDENTIAL);
        }

        jwtProvider.generateAndStoreKey(user.getId());

        String accessToken = jwtProvider.generateAccessToken(user.getId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getId());

        redisService.saveRefreshToken(user.getId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken);
    }

    // Refresh Token 검증 (Redis 비교만)
    public void validateRefreshToken(Long userId, String refreshToken) {
        String savedToken = redisService.getRefreshToken(userId);
        if (savedToken == null || !savedToken.equals(refreshToken)) {
            throw new AuthApiException(ResponseCode.REFRESH_TOKEN_EXPIRED);
        }
        // JWT 서명 검증은 Filter에서 이미 했으므로 여기선 Redis 비교만
    }
    // Refresh Token 재발급 (Controller에서 호출)
    public LoginResponse reissueTokens(Long userId, String refreshToken) {
        // 1. JWT 서명 검증
        Long validatedUserId = jwtProvider.validateToken(userId, refreshToken);

        // 2. userId 일치 확인
        if (!validatedUserId.equals(userId)) {
            throw new AuthApiException(ResponseCode.INVALID_TOKEN);
        }

        // 3. Redis 비교 검증
        validateRefreshToken(userId, refreshToken);

        // 4. 새 토큰 생성
        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        // 5. Redis 업데이트
        redisService.saveRefreshToken(userId, newRefreshToken);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }
    // 로그아웃
    public void logout(Long userId) {
        redisService.deleteRefreshToken(userId);
    }
}