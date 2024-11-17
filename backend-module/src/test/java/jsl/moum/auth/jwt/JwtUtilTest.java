package jsl.moum.auth.jwt;

import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

class JwtUtilTest {

    private JwtUtil jwtUtil;
    private String secretKey = "vmfhaltmskdlstkfkdgodyroqkfwkdbalroqkfwkdbalaaaaaaaaaaaaaaaabbbbb";
    private String validToken;
    private String expiredToken;
    private String malformedToken;

    private String mockUsername = "mockUser";
    private String mockTokenCategory = "access";
    private String mockRole = "ROLE_USER";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil(secretKey);
        validToken = jwtUtil.createJwt(mockTokenCategory, mockUsername, mockRole, 100000);
        expiredToken = jwtUtil.createJwt(mockTokenCategory, mockUsername, mockRole, -1000);
        malformedToken = "malformed.token.fortest";
    }


    @Test
    @DisplayName("토큰에서 사용자 이름 추출")
    void get_username_from_token() {
        String username = jwtUtil.getUsername(validToken);
        assertThat(username).isEqualTo(mockUsername);
    }

    @Test
    @DisplayName("유효한 토큰에서 role 추출")
    void get_role_from_token() {
        String role = jwtUtil.getRole(validToken);
        assertThat(role).isEqualTo(mockRole);
    }

    @Test
    @DisplayName("토큰 만료 여부 확인")
    void token_isExpired() {
        assertFalse(jwtUtil.isExpired(validToken));
        //assertTrue(jwtUtil.isExpired(expiredToken));
        ErrorResponse response = jwtUtil.validateToken(expiredToken);
        assertEquals(ErrorCode.JWT_TOKEN_EXPIRED.getCode(), response.getCode());
    }

    @Test
    @DisplayName("JWT 생성")
    void create_jwt() {
        String token = jwtUtil.createJwt(mockTokenCategory, mockUsername, mockRole, 100000);
        assertNotNull(token);
    }

    @Test
    @DisplayName("토큰 카테고리 추출")
    void get_category_from_token() {
        String category = jwtUtil.getCategory(validToken);
        assertThat(category).isEqualTo(mockTokenCategory);
    }

    @Test
    @DisplayName("유효한 토큰 검증")
    void validate_valid_token() {
        ErrorResponse response = jwtUtil.validateToken(validToken);
        assertThat(response).isNull();
    }

    @Test
    @DisplayName("만료된 토큰 검증")
    void validate_expired_token() {
        ErrorResponse response = jwtUtil.validateToken(expiredToken);
        assertThat(response).isNotNull();
        assertEquals(ErrorCode.JWT_TOKEN_EXPIRED.getCode(), response.getCode());
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 검증")
    void validate_malformed_token() {
        ErrorResponse response = jwtUtil.validateToken(malformedToken);
        assertThat(response).isNotNull();
        assertEquals(ErrorCode.JWT_TOKEN_INVALID.getCode(), response.getCode());
    }

    @Test
    @DisplayName("지원되지 않는 토큰 형식 검증")
    void validate_unsupported_token() {
        String unsupportedToken = "unsupportedTokenFormat";
        ErrorResponse response = jwtUtil.validateToken(unsupportedToken);
        assertThat(response).isNotNull();
        assertEquals(ErrorCode.JWT_TOKEN_INVALID.getCode(), response.getCode());
    }

    @Test
    @DisplayName("잘못된 형식의 토큰 검증 (빈 토큰, 잘못된 토큰)")
    void validate_invalid_argument_token() {
        String invalidToken = "";
        ErrorResponse response = jwtUtil.validateToken(invalidToken);

        assertThat(response).isNotNull();
        assertEquals(ErrorCode.INVALID_INPUT_VALUE.getCode(), response.getCode());
    }
}
