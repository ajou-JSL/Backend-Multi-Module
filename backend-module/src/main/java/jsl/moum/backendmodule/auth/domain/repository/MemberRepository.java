package jsl.moum.backendmodule.auth.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import jsl.moum.backendmodule.auth.domain.entity.MemberEntity;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    // 중복 검증용 메소드
    Boolean existsByUsername(String username);

    MemberEntity findByUsername(String username);

    MemberEntity findByEmail(String email);
    Boolean existsByEmail(String email);
}
