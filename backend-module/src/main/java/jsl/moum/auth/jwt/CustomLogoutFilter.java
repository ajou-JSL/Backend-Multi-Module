package jsl.moum.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jsl.moum.auth.domain.repository.RefreshRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.GenericFilterBean;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.ErrorResponse;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RequiredArgsConstructor
@Slf4j
public class CustomLogoutFilter extends GenericFilterBean {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        doFilter((HttpServletRequest) request, (HttpServletResponse) response, chain);
    }

    private void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws IOException, ServletException {

        // path and method verification
        // logout 요청이 아니면 다음 필터로 보냄
        String requestUri = request.getRequestURI();
        if (!requestUri.matches("^\\/logout$")) {
            filterChain.doFilter(request, response);
            return;
        }

        // POST 요청이 아니면 다음 필터로 보냄
        String requestMethod = request.getMethod();
        if (!requestMethod.equals("POST")) {
            filterChain.doFilter(request, response);
            return;
        }

        // Access 토큰 검증 추가
        String accessToken = request.getHeader("access");
        if (accessToken == null || accessToken.isEmpty()) {
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.JWT_TOKEN_INVALID);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }

        try {
            jwtUtil.validateToken(accessToken);
        } catch (JwtException e) {
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.JWT_TOKEN_INVALID);
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }

        // get refresh token
        String refresh = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    refresh = cookie.getValue();
                }
            }
        }

        // refresh null check
        if (refresh == null || refresh.isEmpty()) {
            log.info("===== 로그아웃 직후에 이 로그 뜨면 refresh null check 수정해야함");
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.MEMBER_ALREADY_LOGOUT);
            response.setStatus(errorResponse.getStatus());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }

        // 만료 여부 체크
        try {
            jwtUtil.isExpired(refresh);
        } catch (ExpiredJwtException e) {
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.JWT_TOKEN_EXPIRED);
            response.setStatus(errorResponse.getStatus());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }

        // 토큰이 refresh인지 확인 (payload에있음)
        String category = jwtUtil.getCategory(refresh);
        if (!category.equals("refresh")) {
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.REFRESH_TOKEN_INVALID);
            response.setStatus(errorResponse.getStatus());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }

        // DB
        Boolean isExist = refreshRepository.existsByRefresh(refresh);
        if (!isExist) {
            ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.MEMBER_NOT_EXIST);
            response.setStatus(errorResponse.getStatus());
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
            return;
        }

        // 로그아웃
        // Refresh 토큰 DB에서 제거
        refreshRepository.deleteByRefresh(refresh);

        // Refresh 토큰 Cookie 값을 0으로 설정
        Cookie cookie = new Cookie("refresh", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        String username = jwtUtil.getUsername(accessToken);

        Map<String, Object> userInfo = new HashMap<>();
        //userInfo.put("id", userId);
        userInfo.put("username", username);

        // 로그아웃 성공 응답
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.LOGOUT_SUCCESS, userInfo);
        response.setStatus(HttpStatus.OK.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(resultResponse));
    }
}
