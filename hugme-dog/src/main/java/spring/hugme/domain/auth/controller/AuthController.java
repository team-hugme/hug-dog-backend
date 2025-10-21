package spring.hugme.domain.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import spring.hugme.domain.auth.dto.LoginRequest;
import spring.hugme.domain.auth.dto.LoginResponse;
import spring.hugme.domain.auth.dto.UserRequestDto;
import spring.hugme.domain.auth.service.AuthService;
import spring.hugme.global.response.CommonApiResponse;
import spring.hugme.global.response.ResponseCode;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 로그인
    @PostMapping("/login")
    public CommonApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("로그인 요청: userId={}", request.getUserId());
        LoginResponse response = authService.login(request.getUserId(), request.getPassword());
        return CommonApiResponse.success(ResponseCode.OK, "정상적으로 로그인이 완료되었습니다.", response);
    }

    // 회원가입
    @PostMapping("/signup")
    public CommonApiResponse<Map<String, String>> signup(@RequestBody @Valid UserRequestDto.SignUp dto) {
        log.info("회원가입 요청: userId={}", dto.getUserId());
        Map<String, String> data = authService.signup(dto);
        return CommonApiResponse.success(ResponseCode.CREATED, "정상적으로 회원가입이 완료되었습니다.", data);
    }

    // 토큰 재발급
    @PostMapping("/reissue")
    public CommonApiResponse<LoginResponse> reissue(@RequestParam String userId,
                                                    @RequestParam String refreshToken) {
        log.info("토큰 재발급 요청: userId={}", userId);
        LoginResponse response = authService.reissueTokens(userId, refreshToken);
        return CommonApiResponse.success(ResponseCode.OK, "토큰 재발급에 성공했습니다.", response);
    }

    // 로그아웃
    @PostMapping("/logout")
    public CommonApiResponse<Void> logout(@RequestParam String userId) {
        log.info("로그아웃 요청: userId={}", userId);
        authService.logout(userId);
        return CommonApiResponse.success(ResponseCode.OK, "정상적으로 로그아웃되었습니다.");
    }
}