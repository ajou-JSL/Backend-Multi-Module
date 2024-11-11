package jsl.moum.custom;

import org.springframework.security.test.context.support.WithSecurityContext;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@WithSecurityContext(factory = WithNoAuthUserSecurityContextFactory.class)
public @interface WithNoAuthUser {
    String email() default "noauth@user.com";
    String username() default "noauthuser";
}
