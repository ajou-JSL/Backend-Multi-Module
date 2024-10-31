package study.moum.community.article.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.community.article.domain.article.ArticleEntity;

public class ArticleDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Request{
        private int id;

        @NotEmpty @NotNull
        private String title;

        @NotNull
        private ArticleEntity.ArticleCategories category;

        // ArticleDetails로 빼서 저장해줄거임 request.dto에만 존재
        private String content;

        // 애도 content랑 마찬가지
        private String fileUrl;

        private MemberEntity author;


        public ArticleEntity toEntity(){
            return ArticleEntity.builder()
                    .id(id)
                    .title(title)
                    .category(category)
                    .author(author)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int id;
        private final String title;
        private final ArticleEntity.ArticleCategories category;
        private final int viewCounts;
        private final int commentCounts;
        private final int likeCounts;
        private final String author;

        public Response(ArticleEntity article){
            this.id = article.getId();
            this.title = article.getTitle();
            this.category = article.getCategory();
            this.author = article.getAuthor().getUsername();
            this.viewCounts = article.getViewCount();
            this.commentCounts = article.getCommentCount();
            this.likeCounts = article.getLikesCount();
        }
    }

}
