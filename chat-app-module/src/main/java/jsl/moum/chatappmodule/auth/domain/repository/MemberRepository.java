package jsl.moum.chatappmodule.auth.domain.repository;

import jsl.moum.chatappmodule.auth.domain.entity.MemberEntity;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface MemberRepository extends R2dbcRepository<MemberEntity, Integer> {

    // 중복 검증용 메소드
    Boolean existsByUsername(String username);

    Mono<MemberEntity> findByUsername(String username);

    MemberEntity findByEmail(String email);
    Boolean existsByEmail(String email);
}
