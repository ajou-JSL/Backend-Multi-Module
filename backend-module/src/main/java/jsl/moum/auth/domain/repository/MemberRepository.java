package jsl.moum.auth.domain.repository;

import jsl.moum.auth.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    Boolean existsByUsername(String username);

    MemberEntity findByUsername(String username);

    Boolean existsByEmail(String email);
}
