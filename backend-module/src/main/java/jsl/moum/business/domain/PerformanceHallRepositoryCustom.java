package jsl.moum.business.domain;

import jsl.moum.business.dto.PerformanceHallDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
public interface PerformanceHallRepositoryCustom {

    Page<PerformanceHall> findAllBySearchParams(Pageable pageable, PerformanceHallDto.Search searchParams);

}
