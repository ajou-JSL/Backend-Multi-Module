package jsl.moum.moum.lifecycle.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface LifecyclePerformanceHallRepository extends JpaRepository<LifecyclePerformanceHall, Integer> {

    @Query("SELECT l FROM LifecyclePerformanceHall l WHERE l.moum.id = :moumId")
    List<LifecyclePerformanceHall> findAllByMoumId(@Param("moumId") int moumId);
}
