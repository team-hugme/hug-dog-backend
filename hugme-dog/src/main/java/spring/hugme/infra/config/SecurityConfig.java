package spring.hugme.infra.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.hugme.infra.jwt.JwtAuthenticationFilter;

@Slf4j
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        log.info("SecurityFilterChain 설정 시작");

        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(
                session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 정적 리소스 및 뷰 허용
                .requestMatchers("/", "/view/**", "/css/**", "/js/**", "/images/**").permitAll()

                // 인증 제외 API
                .requestMatchers(
                    "/api/v1/auth/login",
                    "/api/v1/auth/signup",
                    "/api/v1/auth/reissue/**"
                ).permitAll()
                // 그 외 API 요청은 인증 필요
                .requestMatchers("/api/**").authenticated()
                // 그 외 API 요청은 인증 필요
                .anyRequest().authenticated()
            )
            // JWT 필터 등록
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("SecurityFilterChain 설정 완료");
        return http.build();
    }
}
