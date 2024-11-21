package jsl.moum.report.domain;

import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ArticleReportRepository extends JpaRepository<ArticleReport, Integer> {

    @Query("SELECT COUNT(ar) > 0 FROM ArticleReport ar WHERE ar.article.id = :articleId AND ar.reporter.id = :reporterId")
    boolean existsByReporterIdAndArticleId(@Param("reporterId") int reporterId, @Param("articleId") int articleId);

    @Query("SELECT ar FROM ArticleReport ar WHERE ar.reporter.id = :reporterId")
    Page<ArticleReport> findAllByReporterId(@Param("reporterId") int reporterId, Pageable pageable);

    @Query("SELECT ar FROM ArticleReport ar WHERE ar.article.id = :articleId")
    Page<ArticleReport> findAllByArticleId(@Param("articleId") int articleId, Pageable pageable);
}
