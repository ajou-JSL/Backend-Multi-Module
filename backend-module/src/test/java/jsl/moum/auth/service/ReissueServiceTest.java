package jsl.moum.auth.service;

import jsl.moum.auth.jwt.JwtUtil;
import jsl.moum.auth.domain.repository.RefreshRepository;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReissueServiceTest {

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private RefreshRepository refreshRepository;

    @InjectMocks
    private ReissueService reissueService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("재발급 실패 테스트 - 잘못된 리프레시 토큰")
    void reissueFail_InvalidRefreshToken() {
        // given
        String invalidRefreshToken = "invalidToken";
        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("refresh", invalidRefreshToken)});
        when(jwtUtil.isExpired(invalidRefreshToken)).thenReturn(true);

        // then
        CustomException thrown = assertThrows(CustomException.class, () -> {
            reissueService.reissue(request, response);
        });

        assertEquals(ErrorCode.REFRESH_TOKEN_INVALID, thrown.getErrorCode());
    }

    @Test
    @DisplayName("재발급 성공 테스트 - 유효한 리프레시 토큰")
    void reissueSuccess_ValidRefreshToken() {
        // given
        String validRefreshToken = "validRefreshToken";
        String username = "testUser";
        String role = "ROLE_USER";
        String newAccessToken = "newAccessToken";
        String newRefreshToken = "newRefreshToken";

        when(request.getCookies()).thenReturn(new Cookie[]{new Cookie("refresh", validRefreshToken)});
        when(jwtUtil.isExpired(validRefreshToken)).thenReturn(false);
        when(jwtUtil.getCategory(validRefreshToken)).thenReturn("refresh");
        when(jwtUtil.getUsername(validRefreshToken)).thenReturn(username);
        when(jwtUtil.getRole(validRefreshToken)).thenReturn(role);
        when(jwtUtil.createJwt("access", username, role, 360000L)).thenReturn(newAccessToken);
        when(jwtUtil.createJwt("refresh", username, role, 842000L)).thenReturn(newRefreshToken);
        when(refreshRepository.existsByRefresh(validRefreshToken)).thenReturn(true);

        // when
        ResultResponse result = reissueService.reissue(request, response);

        // then
        assertEquals(ResponseCode.REISSUE_SUCCESS.getCode(), result.getCode());
        assertEquals(username, result.getData());

        ArgumentCaptor<Cookie> cookieCaptor = ArgumentCaptor.forClass(Cookie.class);
        verify(response, times(1)).addCookie(cookieCaptor.capture());

        Cookie capturedCookie = cookieCaptor.getValue();
        assertEquals("refresh", capturedCookie.getName());
        assertEquals(newRefreshToken, capturedCookie.getValue());

        verify(refreshRepository).deleteByRefresh(validRefreshToken);
        verify(refreshRepository).save(any());
        verify(response).setHeader("access", newAccessToken);
    }

    @Test
    @DisplayName("재발급 실패 테스트 - 리프레시 토큰이 없거나 유효하지 않음")
    void reissueFail_NoRefreshToken() {
        // given
        when(request.getCookies()).thenReturn(null);

        // then
        CustomException thrown = assertThrows(CustomException.class, () -> {
            reissueService.reissue(request, response);
        });

        assertEquals(ErrorCode.REFRESH_TOKEN_INVALID, thrown.getErrorCode());
    }
}