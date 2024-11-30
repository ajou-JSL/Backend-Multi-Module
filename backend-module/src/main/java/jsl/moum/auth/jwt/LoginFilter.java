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
import jsl.moum.common.CommonService;
import jsl.moum.global.error.exception.CustomException;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.ErrorResponse;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;

import java.io.IOException;
import java.util.*;

@RequiredArgsConstructor
@Slf4j
public class LoginFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final RefreshRepository refreshRepository;
    private final MemberRepository memberRepository;

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {

        //클라이언트 요청에서 username, password 추출
        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // Manually create a list of authorities and add "ROLE_ADMIN"
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(username, password, authorities);
        return authenticationManager.authenticate(authToken);
    }

    //로그인 성공시, jwt발급
    // access -> haeders
    // refresh : cookie
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException {

    String username = authentication.getName();
    int userId = ((CustomUserDetails) authentication.getPrincipal()).getMemberId();
    MemberEntity loginUser = memberRepository.findById(userId)
            .orElseThrow(()-> new CustomException(ErrorCode.MEMBER_NOT_EXIST));
    String name = loginUser.getName();

    if(!loginUser.getActiveStatus()){
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.SIGN_OUT_MEMBER);
        response.setHeader("access", null);
        response.addCookie(createCookie("refresh", null));
        response.setStatus(errorResponse.getStatus());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    } else if(loginUser.getBanStatus()){
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.BANNED_MEMBER);
        response.setHeader("access", null);
        response.addCookie(createCookie("refresh", null));
        response.setStatus(errorResponse.getStatus());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    } else{
        Map<String, String> userInfo = new HashMap<>();
        userInfo.put("id", String.valueOf(userId));
        userInfo.put("name", name);

        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();
        String role = auth.getAuthority();

        String access = jwtUtil.createJwt("access", username, role, 36000000L);
        String refresh = jwtUtil.createJwt("refresh", username, role, 842000L);

        addRefreshEntity(username, refresh, 842000L);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.LOGIN_SUCCESS, userInfo);
        response.setHeader("access", access);
        response.addCookie(createCookie("refresh", refresh));
        response.setStatus(resultResponse.getStatus());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(resultResponse));
        }
    }

    void addRefreshEntity(String username, String refresh, long expiredMs) {

        Date date = new Date(System.currentTimeMillis() + expiredMs);

        RefreshEntity refreshEntity = new RefreshEntity();
        refreshEntity.setUsername(username);
        refreshEntity.setRefresh(refresh);
        refreshEntity.setExpiration(date.toString());

        refreshRepository.save(refreshEntity);
    }

    // 로그인 실패시
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException {
        log.info("unsuccessfulAuthentication response : {}", response);
        ErrorResponse errorResponse = ErrorResponse.of(ErrorCode.LOGIN_FAIL);
        response.setStatus(HttpStatus.BAD_REQUEST.value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(errorResponse));
    }

    private Cookie createCookie(String key, String value) {

        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true); // https경우
        //cookie.setPath("/"); // cookie 적용 범위
        cookie.setHttpOnly(true); // httpOnly -> client단에서 js로 해당 쿠키 접근 못하게 막아야함

        return cookie;
    }
}
