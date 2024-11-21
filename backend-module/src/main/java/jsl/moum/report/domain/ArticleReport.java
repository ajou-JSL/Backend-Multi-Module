package jsl.moum.report.domain;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.community.article.domain.article.ArticleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "article_report")
public class ArticleReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Foreign key reference to the article
    @ManyToOne
    @JoinColumn(name = "article_id", referencedColumnName = "id", nullable = false)
    private ArticleEntity article;

    // Storing the article title directly
    @Column(name = "article_title", nullable = false)
    private String articleTitle;

    // Foreign key reference to the reporter
    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "id", nullable = false)
    private MemberEntity reporter;

    // Storing the reporter's username directly
    @Column(name = "reporter_username", nullable = false)
    private String reporterUsername;

    @Column(name = "type")
    private String type;

    @Column(name = "reply")
    private String reply;

    @Column(name = "details")
    private String details;

    @Column(name = "is_resolved", columnDefinition = "boolean default false")
    private boolean isResolved;
}
