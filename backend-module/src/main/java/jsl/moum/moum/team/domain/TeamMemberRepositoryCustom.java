package jsl.moum.moum.team.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.team.dto.TeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static jsl.moum.moum.team.domain.QTeamMemberEntity.teamMemberEntity;


@Repository
@RequiredArgsConstructor
public class TeamMemberRepositoryCustom {


    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 팀에 속한 멤버인지 여부
     **/
    public boolean existsByTeamAndMember(int teamId, int memberId) {
        long count = jpaQueryFactory
                .selectFrom(teamMemberEntity)
                .where(
                        teamMemberEntity.team.id.eq(teamId)
                                .and(teamMemberEntity.member.id.eq(memberId))
                )
                .fetch().size(); // 멤버의 개수를 가져옴

        return count > 0; // 멤버가 존재하면 true, 아니면 false
    }

    /**
     * 팀을 가지고 있는 멤버인지 여부
     **/
    public boolean hasTeam(int memberId) {
        long count = jpaQueryFactory
                .selectFrom(teamMemberEntity)
                .where(teamMemberEntity.member.id.eq(memberId))
                .fetch().size();

        return count > 0;
    }


    /**
        delete from team_member
        where team_id = :teamId and member_id = :memberId
     **/
    public void deleteMemberFromTeamById(int teamId, int memberId) {
        long deletedCount = jpaQueryFactory
                .delete(teamMemberEntity)
                .where(
                        teamMemberEntity.team.id.eq(teamId)
                                .and(teamMemberEntity.member.id.eq(memberId))
                )
                .execute();

        if (deletedCount == 0) {
            throw new CustomException(ErrorCode.MEMBER_NOT_EXIST);
        }
    }

    /**
        select member
        from team_member
        where team_id =:teamId and member_id =:memberId
     **/
    public TeamMemberEntity findMemberInTeamById(int teamId, int memberId) {
        return jpaQueryFactory
                .select(teamMemberEntity)
                .from(teamMemberEntity)
                .where(teamMemberEntity.team.id.eq(teamId).and(teamMemberEntity.member.id.eq(memberId)))
                .fetchOne();
    }

    /**
        select team_member
        from team_member
        where team_member.team_id =: teamId
     **/
    public List<MemberEntity> findAllMembersByTeamId(int teamId) {
        return jpaQueryFactory
                .select(teamMemberEntity.member)
                .from(teamMemberEntity)
                .where(teamMemberEntity.team.id.eq(teamId))
                .fetch();
    }

    /**
     * delete from team_member
     * where team_id =:teamId
     */
    public void deleteTeamMemberTable(int teamId){
        long deletedCount = jpaQueryFactory
                .delete(teamMemberEntity)
                .where(teamMemberEntity.team.id.eq(teamId))
                .execute();

        if (deletedCount == 0) {
            throw new CustomException(ErrorCode.ILLEGAL_ARGUMENT);
        }
    }

    /**
     * select team
     * from team_member
     * where team_member.member_id=:memberId
     * and team_member.leader_id=:memberId
     */
    public List<TeamDto.Response> findAllTeamsByLeaderId(int memberId) {
        List<TeamEntity> teams = jpaQueryFactory
                .select(teamMemberEntity.team)
                .from(teamMemberEntity)
                .where(
                        teamMemberEntity.member.id.eq(memberId)
                                .and(teamMemberEntity.leaderId.eq(memberId))
                )
                .fetch();

        return teams.stream()
                .map(TeamDto.Response::new)
                .collect(Collectors.toList());
    }

    /**
     * select team
     * from team_member
     * where team_member.member_id=:memberId
     */
    public List<TeamDto.Response> findAllTeamsByMemberId(int memberId) {
        List<TeamEntity> teams = jpaQueryFactory
                .select(teamMemberEntity.team)
                .from(teamMemberEntity)
                .where(
                        teamMemberEntity.member.id.eq(memberId)
                )
                .fetch();

        return teams.stream()
                .map(TeamDto.Response::new)
                .collect(Collectors.toList());
    }
}
