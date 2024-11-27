package jsl.moum.moum.lifecycle.domain.repository;

import jsl.moum.moum.lifecycle.domain.entity.LifecyclePracticeRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LifecyclePracticeRoomRepository extends JpaRepository<LifecyclePracticeRoom, Integer> {

    @Query("SELECT l FROM LifecyclePracticeRoom l WHERE l.moum.id = :moumId")
    List<LifecyclePracticeRoom> findAllByMoumId(@Param("moumId") int moumId);

    @Modifying
    @Query("DELETE FROM LifecyclePracticeRoom l WHERE l.moum.id = :moumId")
    void deleteAllByMoumId(@Param("moumId") int moumId);

    @Modifying
    @Query("DELETE FROM LifecyclePracticeRoom l WHERE l.moum.id = :moumId AND l.id = :id")
    void deleteByIdAndMoumId(@Param("id") int id, @Param("moumId") int moumId);
}
