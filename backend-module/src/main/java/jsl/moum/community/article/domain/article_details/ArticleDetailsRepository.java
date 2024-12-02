package jsl.moum.community.article.domain.article_details;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ArticleDetailsRepository extends JpaRepository<ArticleDetailsEntity, Integer> {

    Optional<ArticleDetailsEntity> findByArticleId(int id);

}
