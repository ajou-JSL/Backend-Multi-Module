package jsl.moum.community.perform.domain.entity;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "performance_member")
public class PerformMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "fk_performance_article_id", nullable = false)
    private PerformArticleEntity performanceArticle;

    @ManyToOne
    @JoinColumn(name = "fk_member_id", nullable = false)
    private MemberEntity member;

    public void assignPerformanceArticle(PerformArticleEntity performanceArticle) {
        this.performanceArticle = performanceArticle;
    }
}
