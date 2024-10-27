package jsl.moum.backendmodule.community.likes.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import jsl.moum.backendmodule.auth.domain.entity.MemberEntity;
import jsl.moum.backendmodule.community.article.domain.article.ArticleEntity;
import jsl.moum.backendmodule.community.likes.domain.LikesEntity;

public class LikesDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Request{
        private MemberEntity member;
        private ArticleEntity article;

        public LikesEntity toEntity(){
            return LikesEntity.builder()
                    .member(member)
                    .article(article)
                    .build();
        }

    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int likesId;
        private final int memberId;
        private final int articleId;

        public Response(LikesEntity likesEntity) {
            this.likesId = likesEntity.getId();
            this.memberId = likesEntity.getMember().getId();
            this.articleId = likesEntity.getArticle().getId();
        }
    }
}
