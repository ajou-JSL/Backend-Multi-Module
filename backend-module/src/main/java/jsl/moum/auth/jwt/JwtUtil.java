package jsl.moum.auth.jwt;

import io.jsonwebtoken.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SignatureException;
import java.util.Date;

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

    // 토큰 유효성 검사 메서드 추가
    public void validateToken(String token) throws JwtException {
        try {
            // 토큰 파싱 및 서명 검증
            Jwts.parser()
                    .setSigningKey(secretKey)
                    .build()
                    .parseClaimsJws(token);
        } catch (MalformedJwtException e) {
            // JWT 형식이 잘못된 경우
            throw new JwtException("Invalid JWT token", e);
        } catch (ExpiredJwtException e) {
            // JWT가 만료된 경우
            throw new JwtException("Expired JWT token", e);
        } catch (UnsupportedJwtException e) {
            // 지원되지 않는 JWT 형식인 경우
            throw new JwtException("Unsupported JWT token", e);
        } catch (IllegalArgumentException e) {
            // 토큰이 비어있거나 잘못된 경우
            throw new JwtException("JWT token compact of handler are invalid", e);
        }
    }

}