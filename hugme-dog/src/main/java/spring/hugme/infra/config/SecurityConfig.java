package spring.hugme.infra.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import spring.hugme.infra.filter.JwtAuthenticationFilter;

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
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> {
                    log.info("URL 권한 설정");
                    auth
                            // ✅ 페이지 경로는 모두 허용 (JavaScript에서 토큰 검증)
                            .requestMatchers(
                                    "/",
                                    "/v1/auth/login",
                                    "/v1/auth/signup",
                                    "/v1/auth/main",      // ✅ 추가!
                                    "/css/**",
                                    "/js/**",
                                    "/images/**"
                            ).permitAll()
                            // API 경로만 인증 필요
                            .requestMatchers("/v1/api/**").authenticated()
                            .requestMatchers("/v1/auth/reissue").authenticated()
                            .requestMatchers("/v1/auth/logout").authenticated()
                            // 나머지는 인증 필요
                            .anyRequest().authenticated();
                })
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        log.info("SecurityFilterChain 설정 완료");
        return http.build();
    }
}