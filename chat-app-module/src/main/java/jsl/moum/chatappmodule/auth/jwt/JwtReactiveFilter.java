package jsl.moum.chatappmodule.auth.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import jsl.moum.chatappmodule.auth.domain.CustomReactiveUserDetailsService;
import jsl.moum.chatappmodule.auth.domain.CustomUserDetails;
import jsl.moum.chatappmodule.auth.domain.entity.MemberEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtReactiveFilter implements WebFilter {

    private final JwtUtil jwtUtil;
    private final CustomReactiveUserDetailsService userDetailsService;

    /**
     * Process the Web request and (optionally) delegate to the next
     * {@code WebFilter} through the given {@link WebFilterChain}.
     *
     * @param exchange the current server exchange
     * @param chain    provides a way to delegate to the next filter
     * @return {@code Mono<Void>} to indicate when request processing is complete
     *
     * the `Void` class is an uninstantiable placeholder class to hold a reference to the `Class` object
     * representing the Java keyword void.
     */

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {

        log.info("Begin JWTFilter _____________________________________________");
        log.info("Requested URL : {}", exchange.getRequest().getURI());

        final String username;
        final String role;
        final String category;
        final String accessToken = exchange.getRequest().getHeaders().getFirst("access");

        if(accessToken == null){
            return unauthorizedResponse(exchange);
        }

        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            return unauthorizedResponse(exchange);
        }

        // If not access token, return unauthorized
        category = jwtUtil.getCategory(accessToken);
        if(!category.equals("access")){
            return unauthorizedResponse(exchange);
        }

        username = jwtUtil.getUsername(accessToken);
        role = jwtUtil.getRole(accessToken);

        MemberEntity memberEntity = new MemberEntity();
        memberEntity.setUsername(username);
        memberEntity.setRole(role);
        Mono<UserDetails> userDetailsMono = this.userDetailsService.findByUsername(username);

//        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());
//        SecurityContextHolder.getContext().setAuthentication(authentication);

        /**
         *
         * Add redirectURI to login page on unauthorizedResponse and errorResponse
         * Add .doFinally() method
         * Add Redis DB data retrieval and storage
         *
         * */

        return userDetailsMono
                .flatMap(userDetails -> {
                    log.info("JwtReactiveFilter : flatMap initiated");
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    log.info("JwtReactiveFilter authToken : {}", authToken);

                    SecurityContext context = new SecurityContextImpl(authToken);
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("JwtReactiveFilter : Completed userDetailsMono flatMap");

                    // Write to ReactiveSecurityContextHolder as defined in SecurityBeansConfig.java
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(context)));
                })
                .doOnNext(userDetails -> {
                    log.info("JwtReactiveFilter : Found user : {}", userDetails);
                    // Also add Redis methods here?
                })
                .doOnError(error -> {
                    if (error instanceof ResponseStatusException) {
                        ResponseStatusException responseStatusException = (ResponseStatusException) error;
                        if (responseStatusException.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                            log.error("User not found for token : {}", error.getMessage());
                        } else {
                            log.error("Unexpected error during JWT authentication : {}", error.getMessage());
                        }
                    } else {
                        log.error("Unexpected error during JWT authentication : {}", error.getMessage());
                    }
                })
                .onErrorResume(error -> {
                    // Set redirectURI to login page?
                    return errorResponse(exchange);
                });
    }

    static Mono<Void> unauthorizedResponse(ServerWebExchange exchange) {
        exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
//            Set redirectURI to login page?
//            exchange.getResponse().getHeaders().set(HttpHeaders.LOCATION, "/login");
        return exchange.getResponse().setComplete();
    }

    static Mono<Void> errorResponse(ServerWebExchange exchange){
        exchange.getResponse().setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR);
//            Set redirectURI to login page?
//            exchange.getResponse().getHeaders().set(HttpHeaders.LOCATION, "/login");
        return exchange.getResponse().setComplete();
    }
}
