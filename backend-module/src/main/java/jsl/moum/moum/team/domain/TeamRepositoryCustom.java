package jsl.moum.moum.team.domain;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.article.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static jsl.moum.community.article.domain.article.QArticleEntity.articleEntity;
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
     * 팀 목록 필터링 조회하기  랭킹순조회, 멤버수순조회, 검색조회(장르+팀이름)
     */

    /**
     * 필터링으로 게시글 목록 조회하기
     */
//    public List<TeamEntity> searchTeamsWithFiltering(TeamDto.SearchDto dto, int page, int size) {
//        List<TeamEntity> teams = jpaQueryFactory
//                .selectFrom(teamEntity)
//                .where(
//                        whereConditions(dto)
//                )
//                .orderBy(
//                        orderByConditions(dto).toArray(new OrderSpecifier[0])
//                )
//                .offset((long) page * size)
//                .limit(size)
//                .fetch();
//        return teams;
//    }
//
//    private BooleanExpression whereConditions(TeamDto.SearchDto dto) {
//        BooleanExpression condition = Expressions.asBoolean(true).isTrue();
//
//        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
//            condition = condition.and(
//                    articleEntity.title.containsIgnoreCase(dto.getKeyword())
//            );
//        }
//
//        // 생성일
//        if (dto.getCreatedAt() != null) {
//            condition = condition.and(articleEntity.createdAt.after(dto.getCreatedAt()));
//        }
//
//        // 카테고리
//        if (dto.getCategory() != null) {
//            condition = condition.and(articleEntity.category.eq(dto.getCategory()));
//        }
//
//        // 장르
//        if (dto.getGenre() != null) {
//            condition = condition.and(articleEntity.genre.eq(dto.getGenre()));
//        }
//
//
//        return condition;
//    }
//
//    private List<OrderSpecifier<?>> orderByConditions(ArticleDto.SearchDto dto) {
//        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();
//
//        // 최신순
//        if (dto.getFilterByCreatedAt()) {
//            orderSpecifiers.add(articleEntity.createdAt.desc());
//        }
//
//        // 좋아요순
//        if (dto.getFilterByLikesCount()) {
//            orderSpecifiers.add(articleEntity.likesCount.desc());
//        }
//
//        // 조회수순
//        if (dto.getFilterByViewCount()) {
//            orderSpecifiers.add(articleEntity.viewCount.desc());
//        }
//
//        // 댓글수순
//        if (dto.getFilterByCommentsCount()) {
//            orderSpecifiers.add(articleEntity.commentsCount.desc());
//        }
//
//        // 조건이 하나라도 있으면 리스트 반환, 없으면 기본 정렬 (최신순)
//        if (orderSpecifiers.isEmpty()) {
//            orderSpecifiers.add(articleEntity.createdAt.desc());
//        }
//
//        return orderSpecifiers;
//    }
}
