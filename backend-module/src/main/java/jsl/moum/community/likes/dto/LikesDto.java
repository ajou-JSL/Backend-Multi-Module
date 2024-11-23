package jsl.moum.community.likes.dto;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import jsl.moum.community.likes.domain.LikesEntity;

public class LikesDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Request{
        private MemberEntity member;
        private ArticleEntity article;
        private PerformArticleEntity performArticle;

        public LikesEntity toEntity(){
            return LikesEntity.builder()
                    .member(member)
                    .article(article)
                    .performArticle(performArticle)
                    .build();
        }

    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int likesId;
        private final Integer memberId;
        private final Integer articleId;
        private final Integer performArticleId;

        public Response(LikesEntity likesEntity) {
            this.likesId = likesEntity.getId();
            this.memberId = likesEntity.getMember().getId();
            this.articleId = (likesEntity.getArticle() != null && likesEntity.getArticle().getId() != null)
                    ? likesEntity.getArticle().getId()
                    : null;
            this.performArticleId = (likesEntity.getPerformArticle() != null && likesEntity.getPerformArticle().getId() != null)
                    ? likesEntity.getPerformArticle().getId()
                    : null;
        }

    }
}
