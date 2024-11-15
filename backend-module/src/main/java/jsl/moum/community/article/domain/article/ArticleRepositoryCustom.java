package jsl.moum.community.article.domain.article;

import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.community.article.dto.ArticleDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

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
                .orderBy(articleEntity.viewCount.add(articleEntity.commentCount)
                        .add(Expressions.numberTemplate(Long.class, "datediff(now(), {0})", articleEntity.createdAt).multiply(1.5))
                        .desc()
                )
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }



}
