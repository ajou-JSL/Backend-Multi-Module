package jsl.moum.business.domain;

import jsl.moum.business.dto.PerformanceHallDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceHallRepository extends JpaRepository<PerformanceHall, Integer>, PerformanceHallRepositoryCustom {
}
