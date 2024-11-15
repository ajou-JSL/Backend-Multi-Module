package jsl.moum.moum.team.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static jsl.moum.moum.team.domain.QTeamEntity.teamEntity;
import static jsl.moum.moum.team.domain.QTeamMemberEntity.teamMemberEntity;

@Repository
@RequiredArgsConstructor
public class TeamRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     생성한 팀 개수 찾기
     select COUNT(*)
     from team
     where leader_id =: leaderId;
     */
    public long countCreatedTeamByMemberId(int leaderId){
        return jpaQueryFactory
                .selectFrom(teamEntity)
                .where(teamEntity.leaderId.eq(leaderId))
                .fetch().size();
    }
}
