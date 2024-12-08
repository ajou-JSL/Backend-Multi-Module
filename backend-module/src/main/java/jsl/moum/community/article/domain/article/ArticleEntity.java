package jsl.moum.community.article.domain.article;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.community.article.dto.UpdateArticleDto;
import jsl.moum.report.domain.ArticleReport;
import jsl.moum.report.domain.TeamReport;
import lombok.*;
import org.springframework.security.core.parameters.P;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(
        name = "article",
        indexes = {
                @Index(name = "idx_article_view_comment_created_at", columnList = "view_count, comments_count, created_at DESC"),
        }
)
public class ArticleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "fk_article_author_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity author;

    @NotNull @NotEmpty
    @Column(name = "title", nullable = false)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ArticleCategories category;

    @Column(name = "view_count")
    private int viewCount;

    @Column(name = "likes_count")
    private int likesCount;

    @Column(name = "comments_count")
    private int commentsCount;

    @Column(name = "genre", nullable = false)
    @Enumerated(EnumType.STRING)
    private MusicGenre genre;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "fileUrl")
    private String imageUrl;

    @OneToMany(mappedBy = "article", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ArticleReport> articleReports = new ArrayList<>();

    @PrePersist
    public void createDate(){
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void viewCountUp(){
        this.viewCount += 1;
    }
    public void updateLikesCount(int num) { this.likesCount += num; }
    public void commentsCountUp(){this.commentsCount += 1;}
    public void commentsCountDown(){this.commentsCount -= 1;}

//    public void updateArticle(UpdateArticleDto.Request updateArticleDto) {
//        if (updateArticleDto.getTitle() != null) {
//            this.title = updateArticleDto.getTitle();
//        }
//        if (updateArticleDto.getGenre() != null) {
//            this.genre = updateArticleDto.getGenre();
//        }
//        if (updateArticleDto.getCategory() != null) {
//            this.category = updateArticleDto.getCategory();
//        }
//        this.updatedAt = LocalDateTime.now();
//    }
public void updateArticle(UpdateArticleDto.Request updateArticleDto) {
    if (updateArticleDto == null) {
        return;
    }

    updateTitle(updateArticleDto.getTitle());
    updateGenre(updateArticleDto.getGenre());
    updateCategory(updateArticleDto.getCategory());
    updateFileUrl(updateArticleDto.getFileUrl());

    this.updatedAt = LocalDateTime.now();  // 공통된 업데이트 시간 처리
}

    private void updateTitle(String title) {
        if (title != null) {
            this.title = title;
        }
    }

    private void updateGenre(MusicGenre genre) {
        if (genre != null) {
            this.genre = genre;
        }
    }

    private void updateCategory(ArticleCategories category) {
        if (category != null) {
            this.category = category;
        }
    }

    private void updateFileUrl(String newUrl) {
        if (newUrl != null) {
            this.imageUrl = newUrl;
        }
    }

    @Getter
    @AllArgsConstructor
    public enum ArticleCategories {
        FREE_TALKING_BOARD("자유게시판"),
        RECRUIT_BOARD("모집게시판");

        private final String category;
    }
}
