package jsl.moum.community.article.dto;

import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.community.comment.dto.CommentDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.article.domain.article_details.ArticleDetailsEntity;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ArticleDetailsDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Request{
        private int id;

        private ArticleEntity.ArticleCategories category;
        private String title;
        private String content;

        private String fileUrl;
        private MusicGenre genre;

        public ArticleDetailsEntity toEntity(){
            return ArticleDetailsEntity.builder()
                    .id(id)
                    .content(content)
                    .fileUrl(fileUrl)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int id;
        private final String title;
        private final String category;
        private final String content;
        private final int viewCounts;
        private final int commentsCounts;
        private final int likeCounts;
        private final String author;
        private final String fileUrl;
        private final List<CommentDto.Response> comments;

        public Response(ArticleDetailsEntity articleDetails, ArticleEntity article){
            this.id = article.getId();
            this.title = article.getTitle();
            this.category = article.getCategory().toString();
            this.author = article.getAuthor().getUsername();
            this.viewCounts = article.getViewCount();
            this.commentsCounts = article.getCommentsCount();
            this.likeCounts = article.getLikesCount();
            this.content = articleDetails.getContent();
            this.fileUrl = articleDetails.getFileUrl();
            this.comments = articleDetails.getComments().stream()
                    .map(CommentDto.Response::new)
                    .collect(Collectors.toList());
        }
    }

}
