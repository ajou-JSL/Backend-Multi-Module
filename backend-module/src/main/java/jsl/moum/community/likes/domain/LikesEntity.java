package jsl.moum.community.likes.domain;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "likes",
        uniqueConstraints = {
            @UniqueConstraint(
                    name="likes_article_unique_key",
                    columnNames = {"member_id","article_id"}
            ), @UniqueConstraint(
                        name="likes_perform_article_unique_key",
                        columnNames = {"member_id","perform_article_id"}
                )
        }
)
public class LikesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(/*cascade = CascadeType.ALL,*/ fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne(/*cascade = CascadeType.ALL,*/ fetch = FetchType.LAZY)
    @JoinColumn(name = "article_id", nullable = true)
    private ArticleEntity article;

    @ManyToOne(/*cascade = CascadeType.ALL,*/ fetch = FetchType.LAZY)
    @JoinColumn(name = "perform_article_id", nullable = true)
    private PerformArticleEntity performArticle;
}
