package jsl.moum.community.perform.domain.repository;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.community.perform.dto.PerformArticleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static jsl.moum.community.article.domain.article.QArticleEntity.articleEntity;
import static jsl.moum.community.perform.domain.entity.QPerformArticleEntity.performArticleEntity;

@Repository
@RequiredArgsConstructor
public class PerformArticleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 이달의 공연 게시글 리스트 조회
     * SELECT *
     * FROM perform_article
     * WHERE performance_start_date BETWEEN DATE_FORMAT(NOW(), '%Y-%m-01') AND LAST_DAY(NOW())
     * ORDER BY created_at DESC;
     */
    public Page<PerformArticleEntity> getThisMonthPerformArticles(Pageable pageable) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        List<PerformArticleEntity> content = jpaQueryFactory
                .selectFrom(performArticleEntity)
                .where(
                        performArticleEntity.performanceStartDate.after(java.sql.Date.valueOf(startOfMonth))
                                .and(performArticleEntity.performanceStartDate.before(java.sql.Date.valueOf(endOfMonth)))
                )
                .orderBy(performArticleEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(performArticleEntity.countDistinct())
                .from(performArticleEntity)
                .where(
                        performArticleEntity.performanceStartDate.after(java.sql.Date.valueOf(startOfMonth))
                                .and(performArticleEntity.performanceStartDate.before(java.sql.Date.valueOf(endOfMonth)))
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
     * 모음 이름으로 등록된 공연 게시글의 개수 세기
     * select COUNT(*)
     * from perform_article
     * where fk_moum_id =: moumId;
     */
    public boolean isAlreadyExistPerformArticleByMoumId(int moumId) {
        long count = jpaQueryFactory
                .selectFrom(performArticleEntity)
                .where(performArticleEntity.moum.id.eq(moumId))
                .fetch().size();

        return count > 0;
    }

    /**
     * 모음 ID로 모음의 공연게시글 찾기
     */
    public PerformArticleEntity findPerformArticleByMoumId(int moumId){
        return jpaQueryFactory
                .selectFrom(performArticleEntity)
                .where(performArticleEntity.moum.id.eq(moumId))
                .fetchOne();
    }

    /**
     * 필터링으로 공연 게시글 목록 조회하기
     */
    public Page<PerformArticleEntity> searchPerformArticlesWithFiltering(PerformArticleDto.SearchDto dto, Pageable pageable) {
        List<PerformArticleEntity> content = jpaQueryFactory
                .selectFrom(performArticleEntity)
                .where(
                        whereConditions(dto)
                )
                .orderBy(
                        //orderByConditions(dto)
                        orderByConditions(dto).toArray(new OrderSpecifier[0])
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(performArticleEntity.countDistinct())
                .from(performArticleEntity)
                .where(
                        whereConditions(dto)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    // 공연게시글 : 최신순조회, 검색조회(이름+설명), 좋아요순, 조회수순
    // 장르별, 지역별, 공연시작날짜, 마감날짜
    private BooleanExpression whereConditions(PerformArticleDto.SearchDto dto) {
        BooleanExpression condition = Expressions.asBoolean(true).isTrue();

        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            condition = condition.and(
                    performArticleEntity.performanceName.containsIgnoreCase(dto.getKeyword())
                            .and(performArticleEntity.performanceDescription.containsIgnoreCase(dto.getKeyword()))
            );
        }

        // 지역
        if(dto.getLocation() != null && !dto.getLocation().isEmpty()){
            condition = condition.and(
                    performArticleEntity.performanceLocation.containsIgnoreCase(dto.getLocation())
            );
        }

        // 시작일
        if (dto.getStartDate() != null) {
            condition = condition.and(performArticleEntity.performanceStartDate.after(dto.getStartDate()));
        }

        // 종료일
        if (dto.getEndDate() != null) {
            condition = condition.and(performArticleEntity.performanceEndDate.before(dto.getEndDate()));
        }

        // 장르
        if (dto.getGenre() != null) {
            condition = condition.and(articleEntity.genre.eq(dto.getGenre()));
        }

        return condition;
    }

    private List<OrderSpecifier<?>> orderByConditions(PerformArticleDto.SearchDto dto) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        // 최신순
        if (dto.getFilterByCreatedAt()) {
            orderSpecifiers.add(performArticleEntity.createdAt.desc());
        }

        // 좋아요순
        if (dto.getFilterByLikesCount()) {
            orderSpecifiers.add(performArticleEntity.likesCount.desc());
        }

        // 조회수순
        if (dto.getFilterByViewCount()) {
            orderSpecifiers.add(performArticleEntity.viewCount.desc());
        }

        // 조건이 하나라도 있으면 리스트 반환, 없으면 기본 정렬 (최신순)
        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(performArticleEntity.createdAt.desc());
        }

        return orderSpecifiers;
    }
}
