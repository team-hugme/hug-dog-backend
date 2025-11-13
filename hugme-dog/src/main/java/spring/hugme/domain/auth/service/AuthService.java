package spring.hugme.domain.auth.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    public LoginResponse login(String userId, String password) {
        Member user = userRepository.findByUserId(userId)
            .orElseThrow(() -> new AuthApiException(ResponseCode.NOT_FOUND_USER));

        if (!user.checkPassword(passwordEncoder, password))
            throw new AuthApiException(ResponseCode.BAD_CREDENTIAL);


        String accessToken = jwtProvider.generateAccessToken(user.getUserId());
        String refreshToken = jwtProvider.generateRefreshToken(user.getUserId());

        redisService.saveRefreshToken(user.getUserId(), refreshToken);

        return new LoginResponse(accessToken, refreshToken);
    }

    public String validateAccessToken(String token) {
        return jwtProvider.validateAccessToken(token);
    }

    public String reissueAccessToken(String userId) {
        String refreshToken = redisService.getRefreshToken(userId);
        if (refreshToken == null) {
            throw new AuthApiException(ResponseCode.REFRESH_TOKEN_EXPIRED);
        }

        return jwtProvider.generateAccessToken(userId);
    }

    public void validateRefreshToken(String userId, String refreshToken) {
        String savedToken = redisService.getRefreshToken(userId);
        if (savedToken == null || !savedToken.equals(refreshToken))
            throw new AuthApiException(ResponseCode.REFRESH_TOKEN_EXPIRED);

        jwtProvider.validateRefreshToken(userId, refreshToken);
    }

    public LoginResponse reissueRefreshTokens(String userId, String refreshToken) {
            throw new AuthApiException(ResponseCode.INVALID_TOKEN);
        String validatedUserId = jwtProvider.validateRefreshToken(userId, refreshToken);
        if (!validatedUserId.equals(userId))

        validateRefreshToken(userId, refreshToken);

        String newAccessToken = jwtProvider.generateAccessToken(userId);
        String newRefreshToken = jwtProvider.generateRefreshToken(userId);

        redisService.saveRefreshToken(userId, newRefreshToken);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }
    public void logout(String userId) {
        redisService.deleteRefreshToken(userId);
    }
}