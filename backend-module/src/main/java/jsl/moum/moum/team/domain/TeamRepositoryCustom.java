package jsl.moum.moum.team.domain;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.article.dto.ArticleDto;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.moum.team.dto.TeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static jsl.moum.community.article.domain.article.QArticleEntity.articleEntity;
import static jsl.moum.community.perform.domain.entity.QPerformArticleEntity.performArticleEntity;
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

    /**
     * 팀 목록 필터링 조회하기 : 랭킹순조회, 멤버수순조회, 검색조회(팀이름+팀설명), 장르별, 지역별
     */
    public Page<TeamEntity> searchTeamsWithFiltering(TeamDto.SearchDto dto, Pageable pageable) {
        List<TeamEntity> content = jpaQueryFactory
                .selectFrom(teamEntity)
                .leftJoin(teamMemberEntity).on(teamMemberEntity.team.id.eq(teamEntity.id))
                .where(
                        whereConditions(dto)
                )
                .groupBy(teamEntity.id)  // for 멤버수 순
                .orderBy(
                        orderByConditions(dto).toArray(new OrderSpecifier[0])
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(teamEntity.countDistinct())
                .from(teamEntity)
                .leftJoin(teamMemberEntity).on(teamMemberEntity.team.id.eq(teamEntity.id))
                .where(
                        whereConditions(dto)
                ).groupBy(teamEntity.id);

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression whereConditions(TeamDto.SearchDto dto) {
        BooleanExpression condition = Expressions.asBoolean(true).isTrue();

        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            condition = condition.and(
                    teamEntity.teamName.containsIgnoreCase(dto.getKeyword())
                            .and(teamEntity.description.containsIgnoreCase(dto.getKeyword()))
            );
        }

        // 지역
        if (dto.getLocation() != null) {
            condition = condition.and(teamEntity.location.containsIgnoreCase(dto.getLocation()));
        }

        // 장르
        if (dto.getGenre() != null) {
            condition = condition.and(teamEntity.genre.eq(dto.getGenre()));
        }

        return condition;
    }

    private List<OrderSpecifier<?>> orderByConditions(TeamDto.SearchDto dto) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        // 랭킹 순
        if (dto.getFilterByExp()) {
            orderSpecifiers.add(teamEntity.exp.desc());
        }

        // 멤버수
        if (dto.getFilterByMembersCount()) {
            orderSpecifiers.add(
                    Expressions.numberTemplate(Long.class, "count({0})", teamMemberEntity.id).desc()
            );
        }

        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(teamEntity.createdAt.desc());
        }

        return orderSpecifiers;
    }
}
