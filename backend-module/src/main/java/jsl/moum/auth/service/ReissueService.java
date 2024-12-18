package jsl.moum.auth.service;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jsl.moum.auth.domain.entity.RefreshEntity;
import jsl.moum.auth.domain.repository.RefreshRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jsl.moum.auth.jwt.JwtUtil;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;

import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReissueService {

    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;

    public ResultResponse reissue(HttpServletRequest request, HttpServletResponse response) {

        String refresh = getRefreshTokenFromCookies(request);

        log.info("=================== refresh token :{} ", refresh);
        if (refresh == null || !isRefreshTokenValid(refresh)) {
            throw new CustomException(ErrorCode.REFRESH_TOKEN_INVALID);
        }

        String username = jwtUtil.getUsername(refresh);
        String role = jwtUtil.getRole(refresh);

        String newAccess = jwtUtil.createJwt("access", username, role, 360000L); // 60m
        String newRefresh = jwtUtil.createJwt("refresh", username, role, 842000L); // 24h

        refreshRepository.deleteByRefresh(refresh);
        addRefreshEntity(username, newRefresh, 842000L);

        response.setHeader("access", newAccess);
        response.addCookie(createCookie("refresh", newRefresh));

        return ResultResponse.of(ResponseCode.REISSUE_SUCCESS, username);
    }

    private String getRefreshTokenFromCookies(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals("refresh")) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private boolean isRefreshTokenValid(String refresh) {
        try {
            return !jwtUtil.isExpired(refresh) && "refresh".equals(jwtUtil.getCategory(refresh)) && refreshRepository.existsByRefresh(refresh);
        } catch (Exception e) {
            return false;
        }
    }

    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24 * 60 * 60);
        cookie.setHttpOnly(true);
        return cookie;
    }

    private void addRefreshEntity(String username, String refresh, Long expiredMs) {
        Date date = new Date(System.currentTimeMillis() + expiredMs);
        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());
        refreshRepository.save(refreshEntity);
    }
}
