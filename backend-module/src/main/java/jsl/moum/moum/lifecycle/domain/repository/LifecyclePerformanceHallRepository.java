package jsl.moum.moum.lifecycle.domain.repository;

import jsl.moum.moum.lifecycle.domain.entity.LifecyclePerformanceHall;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LifecyclePerformanceHallRepository extends JpaRepository<LifecyclePerformanceHall, Integer> {

    @Query("SELECT l FROM LifecyclePerformanceHall l WHERE l.moum.id = :moumId")
    List<LifecyclePerformanceHall> findAllByMoumId(@Param("moumId") int moumId);

    @Modifying
    @Query("DELETE FROM LifecyclePerformanceHall l WHERE l.moum.id = :moumId")
    void deleteAllByMoumId(@Param("moumId") int moumId);

    @Modifying
    @Query("DELETE FROM LifecyclePerformanceHall l WHERE l.moum.id = :moumId AND l.id = :id")
    void deleteByIdAndMoumId(@Param("id") int id, @Param("moumId") int moumId);
}
