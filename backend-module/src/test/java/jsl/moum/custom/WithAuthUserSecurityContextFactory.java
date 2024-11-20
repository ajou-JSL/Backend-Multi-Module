package jsl.moum.custom;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;
import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.auth.domain.entity.MemberEntity;

public class WithAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithAuthUser> {

    @Override
    public SecurityContext createSecurityContext(WithAuthUser annotation) {
        String email = annotation.email();
        String role = annotation.role();

        CustomUserDetails user = new CustomUserDetails(MemberEntity.builder()
                .email(email)
                .username("testuser")
                .role(role)
                .build());
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(token);
        return context;
    }
}