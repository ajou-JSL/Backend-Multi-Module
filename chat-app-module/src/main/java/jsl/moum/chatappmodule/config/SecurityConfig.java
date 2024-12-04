package jsl.moum.chatappmodule.config;

import jsl.moum.chatappmodule.auth.jwt.JwtReactiveFilter;
import jsl.moum.chatappmodule.auth.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.web.cors.reactive.CorsConfigurationSource;

@Configuration
@RequiredArgsConstructor
@EnableWebFluxSecurity
@Slf4j
public class SecurityConfig {

    private final JwtUtil jwtUtil;
    private final JwtReactiveFilter jwtReactiveFilter;
    private final CorsConfigurationSource corsConfigurationSource;
    private final ReactiveAuthenticationManager authenticationManager;

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, ServerHttpSecurity httpSecurity) {

        // Disable csrf, also CORS is defined in CorsWebFluxConfig.java
        http.csrf(csrf -> csrf.disable())
                .cors(corsSpec -> corsSpec.configurationSource(corsConfigurationSource));

        http.httpBasic(httpBasicSpec -> httpBasicSpec.disable())
                .formLogin(formLoginSpec -> formLoginSpec.disable());

        http.authorizeExchange(exchange -> exchange
                .pathMatchers("/api/chat/test**","/api/chat/health-check").permitAll()
                .pathMatchers("/api/**").authenticated()
                .anyExchange().authenticated());

        http.addFilterAt(jwtReactiveFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .authenticationManager(authenticationManager)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance());

        /**
         * Add exceptionHandling to redirect to login page if not authenticated
         */
//        http.exceptionHandling(exceptionHandling ->
//                        exceptionHandling.authenticationEntryPoint(new RedirectServerAuthenticationEntryPoint(LOGIN_REDIRECT_URI)))
        http.logout(logoutSpec -> logoutSpec.disable());

        return http.build();
    }

}
