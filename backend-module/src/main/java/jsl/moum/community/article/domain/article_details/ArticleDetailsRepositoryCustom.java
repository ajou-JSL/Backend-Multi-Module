package jsl.moum.community.article.domain.article_details;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Repository;
import jsl.moum.community.article.domain.article.ArticleEntity;
import org.springframework.data.domain.Page;


import java.util.List;
import java.util.Optional;

import static io.jsonwebtoken.lang.Strings.hasText;
import static jsl.moum.community.article.domain.article.QArticleEntity.*;
import static jsl.moum.community.article.domain.article_details.QArticleDetailsEntity.articleDetailsEntity;

@Repository
@RequiredArgsConstructor
@Slf4j
public class ArticleDetailsRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**

     */
    public ArticleDetailsEntity findArticleDetailsByArticleId(int articleId) {

        ArticleDetailsEntity articleDetails = jpaQueryFactory
                .selectFrom(articleDetailsEntity)
                .where(articleDetailsEntity.articleId.eq(articleId))
                .fetchOne();

        if(articleDetails == null){
            log.info(" findArticleDetailsByArticleId 메소드 : articleDetails == null");
            throw new CustomException(ErrorCode.ARTICLE_DETAILS_NOT_FOUND);
        }

        return articleDetails;
    }


    /**
     * 자유게시판 게시글 찾기
     *
       select article.*
       from article
       where category = "FREE_TALKING_BOARD"
       order by created_at desc
     *
     * @return 자유게시판 게시글 리스트
     */
    public Page<ArticleEntity> findFreeTalkingArticles(Pageable pageable) {
        List<ArticleEntity> content = jpaQueryFactory
                .selectFrom(articleEntity)
                .where(articleEntity.category.eq(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD))
                .orderBy(articleEntity.createdAt.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(articleEntity.count())
                .from(articleEntity)
                .where(articleEntity.category.eq(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    /**
     *  모집게시판 게시글 찾기

        select article.*
        from article
        where category = "RECRUIT_BOARD"
        order by created_at desc

     * @return 모집게시판 게시글 리스트
     */
    public Page<ArticleEntity> findRecruitingArticles(Pageable pageable) {
        List<ArticleEntity> content = jpaQueryFactory
                .selectFrom(articleEntity)
                .where(articleEntity.category.eq(ArticleEntity.ArticleCategories.RECRUIT_BOARD))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(articleEntity.createdAt.desc())
                .fetch();

        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(articleEntity.count())
                .from(articleEntity)
                .where(articleEntity.category.eq(ArticleEntity.ArticleCategories.RECRUIT_BOARD));

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    /**
     * 검색 조회(제목, 카테고리)
     *  select a.*
     *  from article a
     *  where lower(a.title) like lower('%:keyword%') and category = :category;
     *
     */
    public Page<ArticleEntity> searchArticlesByTitleKeyword(String keyword, String category, Pageable pageable) {
        List<ArticleEntity> content = jpaQueryFactory
                .selectFrom(articleEntity)
                .where(
                        articleEntity.title.containsIgnoreCase(keyword),
                        isCategory(category)
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // countQuery를 통해 총 데이터 개수 계산
        JPAQuery<Long> countQuery = jpaQueryFactory
                .select(articleEntity.count())
                .from(articleEntity)
                .where(
                        articleEntity.title.containsIgnoreCase(keyword),
                        isCategory(category)
                );

        return PageableExecutionUtils.getPage(content, pageable, countQuery::fetchOne);
    }


    private BooleanExpression isCategory(String category) {
        return hasText(category) ? articleEntity.category.eq(ArticleEntity.ArticleCategories.valueOf(category)) : null;
    }

}
