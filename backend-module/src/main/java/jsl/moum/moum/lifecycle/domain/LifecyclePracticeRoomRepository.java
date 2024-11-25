package jsl.moum.moum.lifecycle.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LifecyclePracticeRoomRepository extends JpaRepository<LifecyclePracticeRoom, Integer> {

    @Query("SELECT l FROM LifecyclePracticeRoom l WHERE l.moum.id = :moumId")
    List<LifecyclePracticeRoom> findAllByMoumId(@Param("moumId") int moumId);

}
