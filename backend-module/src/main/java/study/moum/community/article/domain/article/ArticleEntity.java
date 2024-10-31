package study.moum.community.article.domain.article;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import study.moum.auth.domain.entity.MemberEntity;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "article")
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

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

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

    public void updateArticle(String title, ArticleCategories category)
    {
        this.title = title;
        this.category = category;
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
