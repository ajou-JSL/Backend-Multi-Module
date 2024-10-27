package jsl.moum.backendmodule.community.likes.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import jsl.moum.backendmodule.auth.domain.entity.MemberEntity;
import jsl.moum.backendmodule.community.article.domain.article.ArticleEntity;

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
