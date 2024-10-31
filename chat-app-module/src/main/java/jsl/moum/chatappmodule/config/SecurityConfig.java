package jsl.moum.chatappmodule.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.RedirectServerAuthenticationEntryPoint;
import org.springframework.security.web.server.context.WebSessionServerSecurityContextRepository;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

//    @Bean
//    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ServerHttpSecurity httpSecurity) {
//        log.info("SecurityConfig SecurityWebFilterChain : ____________________________________________");
//        http.csrf(csrf -> csrf.disable())
//                .cors(corsSpec -> corsSpec.disable());
//
//        http.httpBasic(httpBasicSpec -> httpBasicSpec.disable())
//                .formLogin(formLoginSpec -> formLoginSpec.disable());
//
//        http.authorizeExchange(exchange -> exchange
//                        .pathMatchers("/api/**").authenticated()
//                        .anyExchange().permitAll())
//                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
//                .authenticationManager(authenticationManager)
//                .securityContextRepository(new WebSessionServerSecurityContextRepository())
//                .exceptionHandling(exceptionHandling ->
//                        exceptionHandling.authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint(LOGIN_REDIRECT_URI)))
//                .logout(logoutSpec -> logoutSpec.disable());
//
//        log.info("SecurityConfig finish SecurityWebFilterChain");
//        return http.build();
//    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        http.csrf(csrf -> csrf.disable()) // Disable CSRF protection if not needed
                .cors(corsSpec -> corsSpec.disable()); // Disable CORS if not needed

        // Allow all requests without authentication
        http.authorizeExchange(exchange -> exchange
                        .pathMatchers("/api/**").permitAll()
                        .anyExchange().permitAll());

        // Optional: Enable basic auth if needed for debugging

        return http.build();
    }

}
