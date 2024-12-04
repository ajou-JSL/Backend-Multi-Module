package jsl.moum.community.likes.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<LikesEntity, Integer> {

    // 특정 일반게시글에 대한 사용자의 좋아요 삭제
    @Modifying
    @Query(value = "delete from likes where article_id = :articleId and member_id = :memberId", nativeQuery = true)
    void deleteLikeByArticleIdAndMemberId(int articleId, int memberId);

    // 특정 일반게시글에 대해 특정 사용자가 누른 좋아요 찾기
    @Query("SELECT l FROM LikesEntity l WHERE l.article.id = :articleId AND l.member.id = :memberId")
    Optional<LikesEntity> findByArticleIdAndMemberId(@Param("articleId") int articleId, @Param("memberId") int memberId);

    // 특정 공연게시글에 대한 사용자의 좋아요 삭제
    @Modifying
    @Query(value = "delete from likes where perform_article_id = :performArticleId and member_id = :memberId", nativeQuery = true)
    void deleteLikeByPerformArticleIdAndMemberId(int performArticleId, int memberId);

    // 특정 공연게시글에 대해 특정 사용자가 누른 좋아요 찾기
    @Query("SELECT l FROM LikesEntity l WHERE l.performArticle.id = :performArticleId AND l.member.id = :memberId")
    Optional<LikesEntity> findByPerformArticleIdAndMemberId(@Param("performArticleId") int performArticleId, @Param("memberId") int memberId);

}
