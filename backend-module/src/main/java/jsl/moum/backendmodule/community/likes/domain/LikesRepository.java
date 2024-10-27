package jsl.moum.backendmodule.community.likes.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikesRepository extends JpaRepository<LikesEntity, Integer> {

    // 특정 게시글에 대한 사용자의 좋아요 삭제
    @Modifying
    @Query(value = "delete from likes where article_id = :articleId and member_id = :memberId", nativeQuery = true)
    void deleteLikeByArticleIdAndMemberId(int articleId, int memberId);

    // 특정 게시글에 대해 특정 사용자가 누른 좋아요 찾기
    Optional<LikesEntity> findByArticleIdAndMemberId(int articleId, int memberId);
}
