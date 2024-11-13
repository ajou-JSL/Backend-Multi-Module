package jsl.moum.moum.lifecycle.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.moum.lifecycle.dto.LifecycleDto;
import jsl.moum.moum.team.dto.TeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;

import static jsl.moum.auth.domain.entity.QMemberEntity.memberEntity;
import static jsl.moum.moum.lifecycle.domain.QLifecycleEntity.lifecycleEntity;
import static jsl.moum.moum.team.domain.QTeamEntity.teamEntity;
import static jsl.moum.moum.team.domain.QTeamMemberEntity.teamMemberEntity;

@Repository
@RequiredArgsConstructor
public class LifecycleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 유저가 자신이 갖고있는 모음 리스트 찾기
       SELECT l.*
       FROM lifecycle l
       JOIN team t ON l.fk_team_id = t.id
       JOIN team_member tm ON t.id = tm.team_id
       JOIN member m ON tm.member_id = m.id
       WHERE m.username = :username;
     */
    public List<LifecycleEntity> findLifecyclesByUsername(String username) {
        return jpaQueryFactory
                .selectFrom(lifecycleEntity)
                .join(lifecycleEntity.team, teamEntity)
                    .on(teamEntity.id.eq(lifecycleEntity.team.id))
                .join(teamEntity.members, teamMemberEntity)
                    .on(teamEntity.id.eq(teamMemberEntity.team.id))
                .join(teamMemberEntity.member, memberEntity)
                    .on(teamMemberEntity.member.id.eq(memberEntity.id))
                .where(memberEntity.username.eq(username))
                .fetch();

//        return lifecycles.stream()
//                .map(LifecycleDto.Response::new)
//                .collect(Collectors.toList());
    }

    /**
     * 유저가 팀 리더인지 찾기
      SELECT COUNT(*)
      FROM team t
      JOIN member m ON t.leader_id = m.id
      WHERE m.username = :username;
     */
    public boolean isTeamLeaderByUsername(String username){
        long count = jpaQueryFactory
                .selectFrom(teamEntity)
                .join(memberEntity).on(teamEntity.leaderId.eq(memberEntity.id))
                .where(memberEntity.username.eq(username))
                .fetch().size();

        return count > 0;
    }

    /**
     * 팀이 속한 라이프사이클 찾기
       SELECT l.*
       FROM lifecycle l
       JOIN team t ON l.fk_team_id = t.id
       WHERE t.id = :teamId;
     */
    public List<LifecycleEntity> findLifecyclesByTeamId(int teamId){
        return jpaQueryFactory
                .selectFrom(lifecycleEntity)
                .join(teamEntity)
                .on(lifecycleEntity.team.id.eq(teamEntity.id))
                .where(teamEntity.id.eq(teamId))
                .fetch();
    }

    /**
       생성한 라이프사이클 개수 찾기
       SELECT COUNT(*)
       FROM lifecycle
       WHERE leader_id = ?;
     */
    public long countCreatedLifecycleByMemberId(int leaderId){
        return jpaQueryFactory
                .selectFrom(lifecycleEntity)
                .where(lifecycleEntity.leaderId.eq(leaderId))
                .fetch().size();
    }
}
