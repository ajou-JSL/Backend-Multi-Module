package jsl.moum.auth.jwt;

import io.jsonwebtoken.*;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.ErrorResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private final SecretKey secretKey;

    public JwtUtil(@Value("${spring.jwt.secret}")String secret) {
        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
    }

    public String getRole(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    public String createJwt(String category, String username, String role, long expiredMs) {

        return Jwts.builder()
                .claim("category", category) // access? or refresh?
                .claim("username", username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }


    // 토큰 판단용 access? or refresh?
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    public ErrorResponse validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
            return null;

        } catch (MalformedJwtException e) {
            // JWT 형식이 잘못된 경우
            List<ErrorResponse.FieldError> errors = ErrorResponse.FieldError.of("token", token, "Invalid JWT token");
            return ErrorResponse.of(ErrorCode.JWT_TOKEN_INVALID, errors);

        } catch (ExpiredJwtException e) {
            // JWT가 만료된 경우
            List<ErrorResponse.FieldError> errors = ErrorResponse.FieldError.of("token", token, "JWT token expired");
            return ErrorResponse.of(ErrorCode.JWT_TOKEN_EXPIRED, errors);

        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 형식인 경우
            List<ErrorResponse.FieldError> errors = ErrorResponse.FieldError.of("token", token, "Unsupported JWT token");
            return ErrorResponse.of(ErrorCode.JWT_TOKEN_INVALID, errors);

        } catch (IllegalArgumentException e) {
            // 토큰이 비어있거나 잘못된 경우
            List<ErrorResponse.FieldError> errors = ErrorResponse.FieldError.of("token", token, "JWT token compact of handler are invalid");
            return ErrorResponse.of(ErrorCode.INVALID_INPUT_VALUE, errors);
        }
    }
}