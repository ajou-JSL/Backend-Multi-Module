package jsl.moum.business.domain;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.business.dto.PerformanceHallDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@RequiredArgsConstructor
public class PerformanceHallRepositoryImpl implements PerformanceHallRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PerformanceHall> findAllBySearchParams(Pageable pageable, PerformanceHallDto.Search searchParams) {

        String name = searchParams.getName();
        Double latitude = searchParams.getLatitude();
        Double longitude = searchParams.getLongitude();
        Integer minPrice = searchParams.getMinPrice();
        Integer maxPrice = searchParams.getMaxPrice();
        Integer minSize = searchParams.getMinSize();
        Integer maxSize = searchParams.getMaxSize();
        Integer minCapacity = searchParams.getMinCapacity();
        Integer maxCapacity = searchParams.getMaxCapacity();
        Integer minStand = searchParams.getMinStand();
        Integer maxStand = searchParams.getMaxStand();
        Boolean hasPiano = searchParams.getHasPiano();
        Boolean hasAmp = searchParams.getHasAmp();
        Boolean hasSpeaker = searchParams.getHasSpeaker();
        Boolean hasMic = searchParams.getHasMic();
        Boolean hasDrums = searchParams.getHasDrums();

        /**
         *
         * TODO
         *
         */

        return null;
    }
}
