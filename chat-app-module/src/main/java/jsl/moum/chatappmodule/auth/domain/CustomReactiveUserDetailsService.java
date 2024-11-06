package jsl.moum.chatappmodule.auth.domain;

import jsl.moum.chatappmodule.auth.domain.entity.MemberEntity;
import jsl.moum.chatappmodule.auth.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@Slf4j
@RequiredArgsConstructor
public class CustomReactiveUserDetailsService implements ReactiveUserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        log.info("CustomReactiveUserDetailsService : findByUsername : {}", username);
        Mono<MemberEntity> memberEntityMono = memberRepository.findByUsername(username);

        return memberEntityMono.map(userAuth -> (UserDetails) userAuth)
                .switchIfEmpty(Mono.error(new UsernameNotFoundException("CustomReactiveUserDetailsService : User not found for username: " + username)))
                .doOnNext(userDetails -> log.info("CustomReactiveUserDetailsService : doOnNext Found user : {}", userDetails))
                .doOnError(error -> log.error("CustomReactiveUserDetailsService : doOnError : {} {}", error.getMessage(), error));
    }
}
