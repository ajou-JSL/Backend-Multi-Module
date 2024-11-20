package jsl.moum.report.domain;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleReportRepository extends JpaRepository<ArticleReport, Integer> {

    @Query("SELECT COUNT(ar) > 0 FROM ArticleReport ar WHERE ar.article.id = :articleId AND ar.reporter.id = :reporterId")
    boolean existsByReporterIdAndArticleId(@Param("reporterId") int reporterId, @Param("articleId") int articleId);

}
