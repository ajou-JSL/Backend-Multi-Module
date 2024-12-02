package jsl.moum.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.entity.RefreshEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.auth.domain.repository.RefreshRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.ErrorResponse;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.Authentication;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;


import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

class LoginFilterTest {

    @InjectMocks
    private LoginFilter loginFilter;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private RefreshRepository refreshRepository;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    private String validUsername = "testUser";
    private String validPassword = "testPassword";
    private String validToken = "valid.token.for.test";
    private int userId = 1;

    private MemberEntity memberEntity;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memberEntity = new MemberEntity();
        memberEntity.setId(userId);
        memberEntity.setUsername(validUsername);
        memberEntity.setName("mockUser");

        PrintWriter writer = mock(PrintWriter.class);
        try {
            when(response.getWriter()).thenReturn(writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("로그인 성공 시 access 토큰과 refresh 토큰 발급 및 응답 설정")
    void successfulAuthentication_whenValidCredentials_thenGenerateTokensAndSetResponse() throws Exception {
        // Given
        String validUsername = "testUser";
        String validToken = "mockedToken";
        int userId = 1;

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setId(userId);
        memberEntity.setUsername(validUsername);

        Authentication authentication = mock(Authentication.class);
        HttpServletRequest request = mock(HttpServletRequest.class);
        HttpServletResponse response = mock(HttpServletResponse.class);
        FilterChain filterChain = mock(FilterChain.class);

        when(authentication.getName()).thenReturn(validUsername);
        when(authentication.getPrincipal()).thenReturn(new CustomUserDetails(memberEntity));
        when(memberRepository.findByUsername(anyString())).thenReturn(memberEntity);

        JwtUtil jwtUtil = mock(JwtUtil.class);
        when(jwtUtil.createJwt("access", validUsername, "ROLE_USER", 36000000L)).thenReturn(validToken);
        when(jwtUtil.createJwt("refresh", validUsername, "ROLE_USER", 842000L)).thenReturn(validToken);

        MemberRepository memberRepository = mock(MemberRepository.class);
        when(memberRepository.findById(userId)).thenReturn(Optional.of(memberEntity));

        // Mock response writer
        StringWriter responseWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(responseWriter);
        when(response.getWriter()).thenReturn(writer);

        // When
        loginFilter.successfulAuthentication(request, response, filterChain, authentication);

        // Then
        verify(response).setHeader("access", validToken);
        verify(response).addCookie(any(Cookie.class));
        verify(response).setStatus(HttpServletResponse.SC_OK);

        String responseBody = responseWriter.toString();
        ObjectMapper objectMapper = new ObjectMapper();
        ResultResponse resultResponse = objectMapper.readValue(responseBody, ResultResponse.class);

        assertEquals(ResponseCode.LOGIN_SUCCESS.getCode(), resultResponse.getCode());
        assertNotNull(resultResponse.getData());
    }

    @Test
    @DisplayName("로그인 실패 시 에러 응답")
    void unsuccessfulAuthentication_whenInvalidCredentials_thenReturnErrorResponse() throws IOException {
        // Given
        AuthenticationException failed = mock(AuthenticationException.class);

        // When
        loginFilter.unsuccessfulAuthentication(request, response, failed);

        // Then
        verify(response).setStatus(HttpServletResponse.SC_BAD_REQUEST);
        verify(response).setContentType("application/json;charset=UTF-8");

        // ArgumentCaptor to capture the response body written to the writer
        ArgumentCaptor<String> errorCaptor = ArgumentCaptor.forClass(String.class);
        verify(response.getWriter()).write(errorCaptor.capture());

        String responseBody = errorCaptor.getValue();
        ErrorResponse errorResponse = new ObjectMapper().readValue(responseBody, ErrorResponse.class);

        assertEquals(ErrorCode.LOGIN_FAIL.getCode(), errorResponse.getCode());
    }

    @Test
    @DisplayName("로그인 시 refresh 토큰 저장")
    void addRefreshEntity_whenValidCredentials_thenSaveRefreshToken() {
        // Given
        String refreshToken = "refresh.token";
        when(jwtUtil.createJwt("refresh", validUsername, "ROLE_USER", 842000L)).thenReturn(refreshToken);

        // When
        loginFilter.addRefreshEntity(validUsername, refreshToken, 842000L);

        // Then
        verify(refreshRepository).save(any(RefreshEntity.class));
    }
}
