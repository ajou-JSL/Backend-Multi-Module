package study.moum.community.article.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.web.multipart.MultipartFile;
import study.moum.community.article.domain.article.ArticleEntity;
import study.moum.community.article.domain.article_details.ArticleDetailsEntity;
import study.moum.community.comment.dto.CommentDto;

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

        // only update request 용도
        private String title;

        // ArticleDetails로 빼서 저장해줄거임 request.dto에만 존재
        private String content;

        private String fileUrl;

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
        private final int commentCounts;
        private final int likeCounts;
        private final String author;
        private List<CommentDto.Response> comments = new ArrayList<>();
        private String fileUrl;

        public Response(ArticleDetailsEntity articleDetails, ArticleEntity article){
            this.id = article.getId();
            this.title = article.getTitle();
            this.category = article.getCategory().toString();
            this.author = article.getAuthor().getUsername();
            this.viewCounts = article.getViewCount();
            this.commentCounts = article.getCommentCount();
            this.likeCounts = article.getLikesCount();
            this.comments = articleDetails.getComments().stream()
                    .map(CommentDto.Response::new)
                    .collect(Collectors.toList());
            this.content = articleDetails.getContent();
            this.fileUrl = articleDetails.getFileUrl();
        }
    }

}
