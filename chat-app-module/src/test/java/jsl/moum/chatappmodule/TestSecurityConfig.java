package jsl.moum.chatappmodule;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@TestConfiguration
public class TestSecurityConfig {
    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http.authorizeExchange(exchange -> exchange
                        .anyExchange().permitAll()) // Allow all requests without authentication
                .csrf(ServerHttpSecurity.CsrfSpec::disable) // Disable CSRF for simplicity in tests
                .build();
    }
}
