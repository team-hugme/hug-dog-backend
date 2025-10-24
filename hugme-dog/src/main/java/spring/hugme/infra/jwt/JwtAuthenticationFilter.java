package spring.hugme.infra.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.hugme.global.error.exceptions.AuthApiException;
import spring.hugme.global.response.CommonApiResponse;
import spring.hugme.global.response.ResponseCode;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;
    // 인증 제외 URL
    private static final List<String> EXCLUDE_URLS = Arrays.asList(
        "/",
        "/main",
        "/view/**",
        "/api/v1/auth/login",
        "/api/v1/auth/signup",
        "/api/v1/auth/reissue/**",
        "/css/**",
        "/js/**",
        "/images/**"
    );
    private final AntPathMatcher pathMatcher = new AntPathMatcher();

    @Override
    protected void doFilterInternal(
        @NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("JWT Filter 처리: {} {}", request.getMethod(), requestURI);

        // 인증 제외 URL이면 JWT 검사 없이 통과
        if (isExcludedUrl(requestURI)) {
            log.info("인증 제외 경로: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        log.info("Authorization Header: {}", header != null ? "있음" : "없음");

        if (header != null && header.startsWith("Bearer ")) {
            String accessToken = header.substring(7);

            try {
                String userId = jwtProvider.validateAccessToken(accessToken);
                log.info("토큰 검증 성공 - UserId: {}", userId);
                List<GrantedAuthority> authorities = List.of(
                    new SimpleGrantedAuthority("ROLE_USER"));

                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authentication);

            } catch (AuthApiException ex) {
                log.error("토큰 검증 실패: {}", ex.getMessage());
                handleAuthException(response, ex);
                return;
            } catch (Exception ex) {
                log.error("토큰 처리 중 예외 발생", ex);
                sendErrorResponse(response, ResponseCode.INVALID_TOKEN, "토큰 처리 중 오류가 발생했습니다.");
                return;
            }
        }

        // SecurityConfig에서 인증 여부 판단하도록 위임
        filterChain.doFilter(request, response);
    }

    // AntPathMatcher를 사용한 정확한 패턴 매칭
    private boolean isExcludedUrl(String requestURI) {
        return EXCLUDE_URLS.stream()
            .anyMatch(pattern -> pathMatcher.match(pattern, requestURI));
    }

    // 토큰 검증 실패 처리
    private void handleAuthException(HttpServletResponse response, AuthApiException ex)
        throws IOException {
        switch (ex.getCode()) {
            case REFRESH_TOKEN_EXPIRED ->
                sendErrorResponse(response, ResponseCode.REFRESH_TOKEN_EXPIRED,
                    "리프레시 토큰이 만료되었습니다. 다시 로그인해주세요.");
            case TOKEN_EXPIRED -> sendErrorResponse(response, ResponseCode.TOKEN_EXPIRED,
                "액세스 토큰이 만료되었습니다.");
            case INVALID_TOKEN -> sendErrorResponse(response, ResponseCode.INVALID_TOKEN,
                "유효하지 않은 토큰입니다.");
            case INVALID_ACCESS_TOKEN ->
                sendErrorResponse(response, ResponseCode.INVALID_ACCESS_TOKEN,
                    "유효하지 않은 액세스 토큰입니다.");
            case MISMATCH_TOKEN -> sendErrorResponse(response, ResponseCode.MISMATCH_TOKEN,
                "토큰 정보가 일치하지 않습니다.");
            case FAILED_TOKEN_PARSE -> sendErrorResponse(response, ResponseCode.FAILED_TOKEN_PARSE,
                "토큰 파싱에 실패했습니다.");
            case NOT_EXIST_PRE_AUTH_CREDENTIAL ->
                sendErrorResponse(response, ResponseCode.NOT_EXIST_PRE_AUTH_CREDENTIAL,
                    "인증 정보가 존재하지 않습니다.");
            default -> sendErrorResponse(response, ResponseCode.INVALID_TOKEN,
                "알 수 없는 토큰 오류입니다.");
        }
    }

    // 공통 에러 응답
    private void sendErrorResponse(HttpServletResponse response, ResponseCode code, String message)
        throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        CommonApiResponse<?> errorResponse = CommonApiResponse.error(code, message);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}