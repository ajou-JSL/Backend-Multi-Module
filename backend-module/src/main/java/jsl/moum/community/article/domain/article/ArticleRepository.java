package jsl.moum.community.article.domain.article;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ArticleRepository extends JpaRepository<ArticleEntity, Integer> {

    // 사용자가 좋아요를 누른 게시글 목록 조회
    /*
        select a.*
        from article a
        inner join likes l
            on a.id = l.article_id
        where l.member_id = ?;
    */
    @Query(value = "select a.* from article a inner join likes l on a.id = l.article_id where l.member_id = :memberId", nativeQuery = true)
    List<ArticleEntity> findLikedArticles(int memberId);
}

