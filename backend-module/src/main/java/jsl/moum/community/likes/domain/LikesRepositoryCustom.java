package jsl.moum.community.likes.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static jsl.moum.community.likes.domain.QLikesEntity.likesEntity;

@Repository
@RequiredArgsConstructor
public class LikesRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /*
        일반게시글에 이미 좋아요 눌렀는지 확인
        select count(*)
        from likes
        where likes.member_id =: memberId
            and likes.article_id =: articleId
     */
    public boolean isAlreadyLikesOnArticle(int memberId, int articleId){
        long count = jpaQueryFactory
                .selectFrom(likesEntity)
                .where(likesEntity.member.id.eq(memberId)
                        .and(likesEntity.article.id.eq(articleId))
                )
                .fetch().size();

        return count > 0;
    }

    /*
        공연게시글에 이미 좋아요 눌렀는지 확인
        select count(*)
        from likes
        where likes.member_id =: memberId
            and likes.article_id =: articleId
     */
    public boolean isAlreadyLikesOnPerformArticle(int memberId, int performArticleId){
        long count = jpaQueryFactory
                .selectFrom(likesEntity)
                .where(likesEntity.member.id.eq(memberId)
                        .and(likesEntity.performArticle.id.eq(performArticleId))
                )
                .fetch().size();

        return count > 0;
    }
}
