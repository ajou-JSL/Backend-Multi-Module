package jsl.moum.community.article.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.community.article.domain.article.ArticleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

public class UpdateArticleDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Request{

        @NotEmpty @NotNull private String title;
        @NotEmpty @NotNull private ArticleEntity.ArticleCategories category;
        private String content;
        private MusicGenre genre;
        private String fileUrl;
    }
}
