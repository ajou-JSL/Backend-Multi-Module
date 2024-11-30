package jsl.moum.auth.domain.repository;

import jsl.moum.auth.domain.entity.MemberEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MemberRepository extends JpaRepository<MemberEntity, Integer> {

    Boolean existsByUsername(String username);

    MemberEntity findByUsername(String username);

    Boolean existsByEmail(String email);

    List<MemberEntity> findAllByBanStatus(boolean banStatus);

    @Query("SELECT m FROM MemberEntity m WHERE m.banStatus = :banStatus")
    Page<MemberEntity> findAllByBanStatusPaged(@Param(value = "banStatus") boolean banStatus, Pageable pageable);

    Long countByBanStatus(boolean banStatus);
}
