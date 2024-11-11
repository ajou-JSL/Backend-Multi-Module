package jsl.moum.custom;

import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.test.context.support.WithSecurityContextFactory;

public class WithNoAuthUserSecurityContextFactory implements WithSecurityContextFactory<WithNoAuthUser> {

    @Override
    public SecurityContext createSecurityContext(WithNoAuthUser annotation) {
        SecurityContext context = SecurityContextHolder.getContext();
        context.setAuthentication(null);
        return context;
    }
}
