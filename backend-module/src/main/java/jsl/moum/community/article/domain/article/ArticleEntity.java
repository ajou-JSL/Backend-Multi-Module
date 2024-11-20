package jsl.moum.community.article.domain.article;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.dto.MusicGenre;
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
        indexes = @Index(name = "idx_article_created_at_desc", columnList = "created_at DESC")
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
    private int commentCount;

    @ElementCollection(targetClass = MusicGenre.class)
    @CollectionTable(name = "article_genre", joinColumns = @JoinColumn(name = "article_id"))
    @Column(name = "genre", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<MusicGenre> genres = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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
    public void commentsCountUp(){this.commentCount += 1;}

    public void updateArticle(String newTitle, ArticleCategories newCategory, List<MusicGenre> newGenres)
    {
        if(newTitle != null){
            this.title = newTitle;
        }
        if (newGenres != null) {
            this.genres = newGenres;
        }
        if(newCategory != null){
            this.category = newCategory;
        }
        this.updatedAt = LocalDateTime.now();
    }

    @Getter
    @AllArgsConstructor
    public enum ArticleCategories {
        FREE_TALKING_BOARD("자유게시판"),
        RECRUIT_BOARD("모집게시판");

        private final String category;
    }
}
