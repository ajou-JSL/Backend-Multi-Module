package jsl.moum.community.likes.dto;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import lombok.*;
import jsl.moum.community.likes.domain.LikesEntity;

public class LikesDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
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
    @Setter
    @AllArgsConstructor
    public static class Toggle{
        private boolean isLiked;
        private int likesCount;
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int likesId;
        private final int memberId;
        private final int articleId;
        private final int performArticleId;

        public Response(LikesEntity likesEntity) {
            this.likesId = likesEntity.getId();
            this.memberId = likesEntity.getMember().getId();
            this.articleId = (likesEntity.getArticle() == null) ? 0 : likesEntity.getArticle().getId();
            this.performArticleId = (likesEntity.getPerformArticle() == null) ? 0 : likesEntity.getPerformArticle().getId();
        }

    }


}
