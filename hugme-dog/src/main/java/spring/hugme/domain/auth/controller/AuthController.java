package spring.hugme.domain.auth.controller;

import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.hugme.domain.auth.dto.request.LoginRequest;
import spring.hugme.domain.auth.dto.request.ReissueRequest;
import spring.hugme.domain.auth.dto.request.SignUpRequest;
import spring.hugme.domain.auth.dto.response.LoginResponse;
import spring.hugme.domain.auth.service.AuthService;
import spring.hugme.global.controller.BaseController;
import spring.hugme.global.response.CommonApiResponse;
import spring.hugme.global.response.ResponseCode;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(BaseController.API_V1 + "/auth")
public class AuthController extends BaseController {

    private final AuthService authService;

    @PostMapping("/login")
    public CommonApiResponse<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        log.info("로그인 요청: userId={}", request.getUserId());
        LoginResponse response = authService.login(request.getUserId(), request.getPassword());
        return CommonApiResponse.success(ResponseCode.OK, "정상적으로 로그인이 완료되었습니다.", response);
    }

    @PostMapping("/signup")
    public CommonApiResponse<Map<String, String>> signup(
        @RequestBody @Valid SignUpRequest.SignUp dto) {
        log.info("회원가입 요청: userId={}", dto.getUserId());
        Map<String, String> data = authService.signup(dto);
        return CommonApiResponse.success(ResponseCode.CREATED, "정상적으로 회원가입이 완료되었습니다.", data);
    }

    @GetMapping("/verify")
    public CommonApiResponse<Map<String, Object>> verify(
        @AuthenticationPrincipal String userId
    ) {
        Map<String, Object> data = new HashMap<>();
        data.put("authenticated", true);
        data.put("userId", userId);
        data.put("timestamp", LocalDateTime.now());
        return CommonApiResponse.success(ResponseCode.OK, "인증 완료", data);
    }

    @PostMapping("/reissue/refresh")
    public CommonApiResponse<LoginResponse> reissueRefresh(@RequestBody ReissueRequest dto) {
        log.info("Refresh 토큰 재발급 요청: userId={}", dto.getUserId());
        LoginResponse response = authService.reissueRefreshTokens(dto.getUserId(),
            dto.getRefreshToken());
        return CommonApiResponse.success(ResponseCode.CREATED,
            "정상적으로 Refresh Token, Access Token이 재발급이 완료되었습니다.", response);
    }

    @PostMapping("/reissue/access")
    public CommonApiResponse<Map<String, String>> reissueAccess(
        @RequestBody Map<String, String> dto) {
        String userId = dto.get("userId");
        log.info("Access 토큰 재발급 요청: userId={}", userId);

        String newAccessToken = authService.reissueAccessToken(
            userId); // AuthService에 Access 전용 로직 필요
        return CommonApiResponse.success(ResponseCode.CREATED, "정상적으로 Access Token 재발급이 완료되었습니다.",
            Map.of("accessToken", newAccessToken));
    }

    @PostMapping("/logout")
    public CommonApiResponse<Void> logout(@AuthenticationPrincipal String tokenUserId) {
        log.info("로그아웃 요청: userId={}", tokenUserId);
        authService.logout(tokenUserId);
        return CommonApiResponse.success(ResponseCode.NO_CONTENT, "정상적으로 로그아웃 완료되었습니다.");
    }
}