package jsl.moum.community.comment.domain;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import lombok.*;
import jsl.moum.community.article.domain.article_details.ArticleDetailsEntity;

import java.time.LocalDateTime;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(
        name = "comment",
        indexes = @Index(name = "idx_comment_article_id", columnList = "fk_article_details_id")
)
public class CommentEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @JoinColumn(name = "fk_comment_author_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private MemberEntity author;

    @JoinColumn(name = "fk_article_details_id")
    @ManyToOne(fetch = FetchType.LAZY)
    private ArticleDetailsEntity articleDetails;

    @Column(name = "content")
    private String content;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void createDate(){
        this.createdAt = LocalDateTime.now();
    }
    public void updateComment(String newContent){
        if(newContent != null){
            this.content = newContent;
        }
    }
}
