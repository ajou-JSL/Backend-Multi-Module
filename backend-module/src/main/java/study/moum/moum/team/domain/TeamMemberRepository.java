package study.moum.moum.team.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import study.moum.auth.domain.entity.MemberEntity;

public interface TeamMemberRepository extends JpaRepository<TeamMemberEntity, Integer> {
}
