package jsl.moum.chatappmodule.auth.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    public boolean isCurrentUser(String userId){
        String principal = getUserPrincipalOrThrow();
        log.info("AuthService isCurrentUser : principal : {}", principal);
        if(userId.equals(principal)){
            return true;
        }
        return false;
    }

    public String getUserPrincipalOrThrow() throws AccessDeniedException {
        log.info("AuthService getUserPrincipalOrThrow");
        Mono<Authentication> auth = getAuthentication();
        String principal = auth.toString();
        log.info("AuthService getUserPrincipalOrThrow : principal : {}", principal);
        if(principal == null || principal.equals("anonymousUser")){
            throw new AccessDeniedException("User login cannot be verified");
        }
        return principal;
    }

    public Mono<Authentication> getAuthentication(){
        log.info("AuthService getAuthentication");
        Mono<Authentication> authMono = ReactiveSecurityContextHolder.getContext()
                .map(securityContext -> securityContext.getAuthentication())
                .doOnNext(auth -> log.info("AuthService getAuthentication : auth : {}", auth));
        return authMono;
    }

}
