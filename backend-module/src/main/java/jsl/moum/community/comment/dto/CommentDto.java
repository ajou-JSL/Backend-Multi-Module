package jsl.moum.community.comment.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jsl.moum.auth.domain.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import jsl.moum.community.article.domain.article_details.ArticleDetailsEntity;
import jsl.moum.community.comment.domain.CommentEntity;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

public class CommentDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request{

        @NotEmpty @NotNull
        private String content;

        private MemberEntity author;
        private ArticleDetailsEntity articleDetails;

        public CommentEntity toEntity(){
            return CommentEntity.builder()
                    .content(content)
                    .articleDetails(articleDetails)
                    .author(author)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{

        private final int commentId;
        private final int articleDetailsId;
        private final String author;
        private final int authorId;
        private final String content;
        private final LocalDateTime createdAt;

        public Response(CommentEntity comment){
            this.commentId = comment.getId();
            this.articleDetailsId = comment.getArticleDetails().getId();
            this.content = comment.getContent();
            this.author = comment.getAuthor().getUsername();
            this.authorId = comment.getAuthor().getId();
            this.createdAt = comment.getCreatedAt();
        }
    }
}
