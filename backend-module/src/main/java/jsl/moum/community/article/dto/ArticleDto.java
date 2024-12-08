package jsl.moum.community.article.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.moum.lifecycle.domain.Music;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import jsl.moum.community.article.domain.article.ArticleEntity;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

public class ArticleDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request{
        private int id;
        @NotEmpty private String title;
        private ArticleEntity.ArticleCategories category;
        @NotEmpty private String content;
        private String fileUrl;
        private MemberEntity author;
        private MusicGenre genre;

        public ArticleEntity toEntity(){
            return ArticleEntity.builder()
                    .title(title)
                    .category(category)
                    .author(author)
                    .genre(genre)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int id;
        private final String title;
        private final String fileUrl;
        private final ArticleEntity.ArticleCategories category;
        private final int viewCounts;
        private final int commentsCounts;
        private final MusicGenre genre;
        private final int likeCounts;
        private final String author;
        private final String authorName;
        private final LocalDateTime createdAt;
        private final LocalDateTime updatedAt;


        public Response(ArticleEntity article){
            this.id = article.getId();
            this.title = article.getTitle();
            this.fileUrl = article.getImageUrl();
            this.category = article.getCategory();
            this.author = article.getAuthor().getUsername();
            this.authorName = article.getAuthor().getName();
            this.viewCounts = article.getViewCount();
            this.commentsCounts = article.getCommentsCount();
            this.genre = article.getGenre();
            this.likeCounts = article.getLikesCount();
            this.createdAt = article.getCreatedAt();
            this.updatedAt = article.getUpdatedAt();
        }
    }

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SearchDto {
        private String keyword;
        private Boolean filterByLikesCount;
        private Boolean filterByViewCount;
        private Boolean filterByCommentsCount;
        private Boolean filterByCreatedAt;
        private LocalDateTime createdAt;
        private ArticleEntity.ArticleCategories category;
        private MusicGenre genre;
    }
}
