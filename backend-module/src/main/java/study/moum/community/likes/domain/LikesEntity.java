package study.moum.community.likes.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.community.article.domain.article.ArticleEntity;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "likes",
        uniqueConstraints = {
            @UniqueConstraint(
                    name="likes_unique_key",
                    columnNames = {"member_id","article_id"}
            )
        }
)
public class LikesEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "article_id")
    private ArticleEntity article;
}
