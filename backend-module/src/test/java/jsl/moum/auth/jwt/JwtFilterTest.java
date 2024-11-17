package jsl.moum.auth.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.auth.domain.entity.MemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class JwtFilterTest {

    @InjectMocks
    private JwtFilter jwtFilter;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private SecurityContext securityContext;

    private String validToken;
    private String expiredToken;
    private String invalidCategoryToken;

    private final String mockUsername = "mockUser";
    private final String mockRole = "ROLE_USER";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        SecurityContextHolder.setContext(securityContext);
        validToken = "validToken";
        expiredToken = "expiredToken";
        invalidCategoryToken = "invalidCategoryToken";

        when(jwtUtil.isExpired(validToken)).thenReturn(false);
        when(jwtUtil.isExpired(expiredToken)).thenThrow(new io.jsonwebtoken.ExpiredJwtException(null, null, null));
        when(jwtUtil.getCategory(validToken)).thenReturn("access");
        when(jwtUtil.getCategory(invalidCategoryToken)).thenReturn("refresh");
        when(jwtUtil.getUsername(validToken)).thenReturn(mockUsername);
        when(jwtUtil.getRole(validToken)).thenReturn(mockRole);
    }

    @Test
    @DisplayName("access 토큰이 없을 경우 필터가 다음으로 넘어간다")
    void filter_whenNoAccessToken_thenPassToNextFilter() throws ServletException, IOException {
        // given
        when(request.getHeader("access")).thenReturn(null);

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    @DisplayName("만료된 access 토큰이 제공되었을 때 401 Unauthorized 응답")
    void filter_whenExpiredToken_thenReturnUnauthorized() throws ServletException, IOException {
        // given
        when(request.getHeader("access")).thenReturn(expiredToken);

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    }

    @Test
    @DisplayName("잘못된 카테고리의 access 토큰이 제공되었을 때 401 Unauthorized 응답")
    void filter_whenInvalidCategoryToken_thenReturnUnauthorized() throws ServletException, IOException {
        // given
        when(request.getHeader("access")).thenReturn(invalidCategoryToken);

        PrintWriter writer = mock(PrintWriter.class);
        when(response.getWriter()).thenReturn(writer);

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        verify(response).setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        verify(writer).print("invalid access token");
    }


    @Test
    @DisplayName("유효한 access 토큰이 제공되었을 때 SecurityContext에 인증 정보가 설정된다")
    void filter_whenValidToken_thenSetAuthentication() throws ServletException, IOException {
        // given
        when(request.getHeader("access")).thenReturn(validToken);

        // when
        jwtFilter.doFilterInternal(request, response, filterChain);

        // then
        ArgumentCaptor<Authentication> authCaptor = ArgumentCaptor.forClass(Authentication.class);
        verify(securityContext).setAuthentication(authCaptor.capture());
        assertNotNull(authCaptor.getValue());
        assertEquals(mockUsername, ((CustomUserDetails) authCaptor.getValue().getPrincipal()).getUsername());
    }

}
