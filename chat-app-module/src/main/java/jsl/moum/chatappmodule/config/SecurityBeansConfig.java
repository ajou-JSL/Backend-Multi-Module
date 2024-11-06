package jsl.moum.chatappmodule.config;

import jsl.moum.chatappmodule.auth.domain.CustomReactiveUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthenticatedReactiveAuthorizationManager;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.web.server.authorization.AuthorizationContext;

@Configuration
@RequiredArgsConstructor
public class SecurityBeansConfig {

    private final CustomReactiveUserDetailsService userDetailsService;

    @Bean
    public ReactiveAuthenticationManager authenticationManager(){
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        return authenticationManager;
    }

    @Bean
    public ReactiveAuthorizationManager<AuthorizationContext> authenticatedAuthorizationManager(){
        return AuthenticatedReactiveAuthorizationManager.authenticated();
    }
}
