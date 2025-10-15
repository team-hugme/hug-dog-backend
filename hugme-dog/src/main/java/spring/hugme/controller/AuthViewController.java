package spring.hugme.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/v1/auth")
public class AuthViewController {

    // 로그인 페이지
    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";  // templates/auth/login.html
    }

    // 회원가입 페이지
    @GetMapping("/signup")
    public String signupPage() {
        // Model에 객체 바인딩 필요 없음 (JavaScript로 처리)
        return "auth/signup";  // templates/auth/signup.html
    }

    // 메인 페이지
    @GetMapping("/main")
    public String mainPage() {
        return "main";  // templates/main.html
    }
}