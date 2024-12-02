package jsl.moum.community.article.domain.article;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.community.article.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static jsl.moum.community.article.domain.article.QArticleEntity.articleEntity;
import static jsl.moum.community.comment.domain.QCommentEntity.commentEntity;
import static jsl.moum.community.likes.domain.QLikesEntity.likesEntity;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


    /**
        // 사용자가 좋아요를 누른 게시글 목록 조회
        select a.*
        from article a
        inner join likes l
            on a.id = l.article_id
        where l.member_id = ?;
    */
    public Page<ArticleEntity> findLikedArticlesByMember(int memberId, Pageable pageable) {

        List<ArticleEntity> content = jpaQueryFactory
                .selectFrom(articleEntity)
                .innerJoin(likesEntity).on(articleEntity.id.eq(likesEntity.article.id))
                .where(likesEntity.member.id.eq(memberId))
                .orderBy(articleEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(articleEntity.count())
                .from(articleEntity)
                .innerJoin(likesEntity).on(articleEntity.id.eq(likesEntity.article.id))
                .where(likesEntity.member.id.eq(memberId));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    /**
        실시간 인기 게시글 조회
        "실시간 인기" 기준 - 조회수,댓글수,생성일자 최근일수록 가중치부여

     SELECT *
     FROM articles a
     LEFT JOIN comments c
        ON a.id = c.article_id
     GROUP BY a.id
     ORDER BY (a.view_count + a.comment_count + DATEDIFF(NOW(), a.created_date) * 1.5) DESC;
     */
    public Page<ArticleEntity> getAllHotArticles(Pageable pageable){
        List<ArticleEntity> content = jpaQueryFactory
                .selectFrom(articleEntity)
                .leftJoin(commentEntity).on(articleEntity.id.eq(commentEntity.articleDetails.articleId))
                .groupBy(articleEntity.id)
                .orderBy(articleEntity.viewCount.add(articleEntity.commentsCount)
                        .add(Expressions.numberTemplate(Long.class, "datediff(now(), {0})", articleEntity.createdAt).multiply(1.5))
                        .desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

//        JPAQuery<Long> countQuery = jpaQueryFactory
//                .select(articleEntity.count())
//                .from(articleEntity);

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(articleEntity.id.countDistinct())
                .from(articleEntity)
                .leftJoin(commentEntity).on(articleEntity.id.eq(commentEntity.articleDetails.articleId));
//                .groupBy(articleEntity.id);


        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    /**
     * 필터링으로 게시글 목록 조회하기
     */
    public Page<ArticleEntity> searchArticlesWithFiltering(ArticleDto.SearchDto dto, Pageable pageable) {
        List<ArticleEntity> content = jpaQueryFactory
                .selectFrom(articleEntity)
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
                .select(articleEntity.countDistinct())
                .from(articleEntity)
                .where(whereConditions(dto));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }

    private BooleanExpression whereConditions(ArticleDto.SearchDto dto) {
        BooleanExpression condition = Expressions.asBoolean(true).isTrue();

        if (dto.getKeyword() != null && !dto.getKeyword().isEmpty()) {
            condition = condition.and(
                    articleEntity.title.containsIgnoreCase(dto.getKeyword())
            );
        }

        // 생성일
        if (dto.getCreatedAt() != null) {
            condition = condition.and(articleEntity.createdAt.after(dto.getCreatedAt()));
        }

        // 카테고리
        if (dto.getCategory() != null) {
            condition = condition.and(articleEntity.category.eq(dto.getCategory()));
        }

        // 장르
        if (dto.getGenre() != null) {
            condition = condition.and(articleEntity.genre.eq(dto.getGenre()));
        }


        return condition;
    }

    private List<OrderSpecifier<?>> orderByConditions(ArticleDto.SearchDto dto) {
        List<OrderSpecifier<?>> orderSpecifiers = new ArrayList<>();

        // 최신순
        if (dto.getFilterByCreatedAt()) {
            orderSpecifiers.add(articleEntity.createdAt.desc());
        }

        // 좋아요순
        if (dto.getFilterByLikesCount()) {
            orderSpecifiers.add(articleEntity.likesCount.desc());
        }

        // 조회수순
        if (dto.getFilterByViewCount()) {
            orderSpecifiers.add(articleEntity.viewCount.desc());
        }

        // 댓글수순
        if (dto.getFilterByCommentsCount()) {
            orderSpecifiers.add(articleEntity.commentsCount.desc());
        }

        // 조건이 하나라도 있으면 리스트 반환, 없으면 기본 정렬 (최신순)
        if (orderSpecifiers.isEmpty()) {
            orderSpecifiers.add(articleEntity.createdAt.desc());
        }

        return orderSpecifiers;
    }

}
