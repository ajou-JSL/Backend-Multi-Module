package jsl.moum.auth.domain.repository;

import jsl.moum.auth.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    // 중복 검증용 메소드
    Boolean existsByUsername(String username);

    MemberEntity findByUsername(String username);

    MemberEntity findByEmail(String email);
    Boolean existsByEmail(String email);
}
