package spring.hugme.infra.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import spring.hugme.infra.error.exceptions.AuthApiException;
import spring.hugme.infra.response.CommonApiResponse;
import spring.hugme.infra.response.ResponseCode;
import spring.hugme.security.jwt.JwtProvider;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;
    private final ObjectMapper objectMapper;

    // 인증이 필요없는 경로들 (HTML 페이지 + 정적 리소스)
    private static final List<String> EXCLUDE_URLS = Arrays.asList(
        "/v1/auth/login",
        "/v1/auth/signup",
        "/v1/auth/main",
        "/css/",
        "/js/",
        "/images/"
    );

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
        @NonNull HttpServletResponse response,
        @NonNull FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        log.info("JWT Filter 처리: {} {}", request.getMethod(), requestURI);

        // 인증이 필요없는 경로는 스킵
        if (isExcludedUrl(requestURI)) {
            log.info("인증 제외 경로: {}", requestURI);
            filterChain.doFilter(request, response);
            return;
        }

        String header = request.getHeader("Authorization");
        log.info("Authorization Header: {}", header != null ? "있음" : "없음");

        if (header != null && header.startsWith("Bearer ")) {
            String accessToken = header.substring(7);
            log.info("Access Token 추출 완료");

            try {
                Long userId = jwtProvider.validateToken(accessToken);
                log.info("토큰 검증 성공 - UserId: {}", userId);

                // SecurityContext에 인증 정보 설정
                UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userId, null, null);
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("SecurityContext 설정 완료");

            } catch (AuthApiException ex) {
                log.error("토큰 검증 실패: {}", ex.getMessage());

                if (ex.getCode() == ResponseCode.TOKEN_EXPIRED) {
                    log.info("Access Token 만료");
                    sendErrorResponse(response, ResponseCode.TOKEN_EXPIRED, "Access Token이 만료되었습니다.");
                } else {
                    log.error("유효하지 않은 토큰");
                    sendErrorResponse(response, ResponseCode.INVALID_TOKEN, "유효하지 않은 토큰입니다.");
                }
                return;
            } catch (Exception ex) {
                log.error("토큰 처리 중 예외 발생", ex);
                sendErrorResponse(response, ResponseCode.INVALID_TOKEN, "토큰 처리 중 오류가 발생했습니다.");
                return;
            }
        } else {
            log.warn("Authorization 헤더가 없거나 Bearer 형식이 아님: {}", requestURI);
            // API 경로가 아니면 그냥 진행 (Spring Security가 처리)
        }

        filterChain.doFilter(request, response);
    }

    // 인증 제외 URL 체크
    private boolean isExcludedUrl(String requestURI) {
        return EXCLUDE_URLS.stream().anyMatch(requestURI::startsWith);
    }

    // 에러 응답 전송
    private void sendErrorResponse(HttpServletResponse response,
        ResponseCode code,
        String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        CommonApiResponse<?> errorResponse = CommonApiResponse.error(code, message, null);
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}