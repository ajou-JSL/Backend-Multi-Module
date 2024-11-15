package jsl.moum.community.article.domain.article_details;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import jsl.moum.community.article.domain.article.ArticleEntity;


import java.util.List;

import static io.jsonwebtoken.lang.Strings.hasText;
import static jsl.moum.community.article.domain.article.QArticleEntity.*;

@Repository
@RequiredArgsConstructor
public class ArticleDetailsRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;


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
    public List<ArticleEntity> findFreeTalkingArticles(int page, int size) {
        return jpaQueryFactory
                .selectFrom(articleEntity)
                .where(articleEntity.category.eq(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD))
                .orderBy(articleEntity.createdAt.desc())
                .offset(page * size) // 페이지에 따른 오프셋 계산
                .limit(size)
                .fetch();
    }


    /**
     *  모집게시판 게시글 찾기

        select article.*
        from article
        where category = "RECRUIT_BOARD"
        order by created_at desc

     * @return 모집게시판 게시글 리스트
     */
    public List<ArticleEntity> findRecruitingdArticles(int page, int size) {
        return jpaQueryFactory
                .selectFrom(articleEntity)
                .where(articleEntity.category.eq(ArticleEntity.ArticleCategories.RECRUIT_BOARD))
                .offset(page * size) // 페이지에 따른 오프셋 계산
                .limit(size)
                .orderBy(articleEntity.createdAt.desc())
                .fetch();
    }

    /**
     * 키워드를 포함하는 게시글을 제목 또는 내용으로 검색
     *  @param keyword 검색할 키워드
     *  @return 검색된 게시글 목록
     *
     *  select a.*
     *  from article a
     *  where lower(a.title) like lower('%:keyword%') and category = :category;
     *
     */
    public List<ArticleEntity> searchArticlesByTitleKeyword(String keyword, String category,int page, int size) {
        return jpaQueryFactory
                .selectFrom(articleEntity)
                .where(
                        articleEntity.title.containsIgnoreCase(keyword),
                        isCategory(category)
                )
                .offset(page * size) // 페이지에 따른 오프셋 계산
                .limit(size)
                .fetch();
    }

    private BooleanExpression isCategory(String category) {
        return hasText(category) ? articleEntity.category.eq(ArticleEntity.ArticleCategories.valueOf(category)) : null;
    }

}
