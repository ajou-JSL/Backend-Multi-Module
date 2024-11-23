package jsl.moum.community.article.domain.article;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.community.article.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

import static jsl.moum.community.article.domain.article.QArticleEntity.articleEntity;
import static jsl.moum.community.comment.domain.QCommentEntity.commentEntity;

@Repository
@RequiredArgsConstructor
public class ArticleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

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
    public List<ArticleEntity> getAllHotArticles(int page, int size){
        return jpaQueryFactory
                .selectFrom(articleEntity)
                .leftJoin(commentEntity).on(articleEntity.id.eq(commentEntity.articleDetails.articleId))
                .groupBy(articleEntity.id)
                .orderBy(articleEntity.viewCount.add(articleEntity.commentsCount)
                        .add(Expressions.numberTemplate(Long.class, "datediff(now(), {0})", articleEntity.createdAt).multiply(1.5))
                        .desc()
                )
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }


    /**
     * 필터링으로 게시글 목록 조회하기
     */
    public List<ArticleEntity> searchArticlesWithFiltering(ArticleDto.SearchDto dto, int page, int size) {
        List<ArticleEntity> articles = jpaQueryFactory
                .selectFrom(articleEntity)
                .where(
                        whereConditions(dto)
                )
                .orderBy(
                        //orderByConditions(dto)
                        orderByConditions(dto).toArray(new OrderSpecifier[0])
                )
                .offset((long) page * size)
                .limit(size)
                .fetch();
        return articles;
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
