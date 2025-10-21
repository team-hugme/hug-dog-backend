package spring.hugme.domain.auth.controller.api;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.hugme.global.response.CommonApiResponse;
import spring.hugme.global.response.ResponseCode;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/v1/api")
@RequiredArgsConstructor
public class ApiController {

    // 토큰 검증용 API
    @GetMapping("/verify")
    public CommonApiResponse<Map<String, Object>> verify() {
        // SecurityContext에서 userId 가져오기
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("authenticated", true);
        data.put("userId", principal);
        data.put("timestamp", LocalDateTime.now());

        return CommonApiResponse.success(ResponseCode.OK, "인증 성공", data);
    }

    // 테스트용 API
    @GetMapping("/test")
    public CommonApiResponse<Map<String, Object>> test() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        Map<String, Object> data = new HashMap<>();
        data.put("message", "API 호출 성공!");
        data.put("userId", principal);
        data.put("timestamp", LocalDateTime.now());
        data.put("status", "OK");

        return CommonApiResponse.success(ResponseCode.OK, "테스트 성공", data);
    }
}