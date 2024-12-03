package jsl.moum.business.domain;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.business.dto.PerformanceHallDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static jsl.moum.business.domain.QPerformanceHall.performanceHall;

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

        OrderSpecifier<?> sortOrderSpecifier = sortByIncludingDist(pageable.getSort(), latitude, longitude);

        List<PerformanceHall> performanceHalls = queryFactory.selectFrom(performanceHall)
                .where(nameContains(name),
                        locationBetween(latitude, longitude),
                        priceBetweenOrMinMax(minPrice, maxPrice),
                        sizeBetweenOrMinMax(minSize, maxSize),
                        capacityBetweenOrMinMax(minCapacity, maxCapacity),
                        standBetweenOrMinMax(minStand, maxStand),
                        hasPiano(hasPiano),
                        hasAmp(hasAmp),
                        hasSpeaker(hasSpeaker),
                        hasMic(hasMic),
                        hasDrums(hasDrums))
                .orderBy(sortOrderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Separate COUNT query for efficiency
        JPAQuery<Long> countQuery = queryFactory
                .select(performanceHall.count())
                .from(performanceHall)
                .where(nameContains(name),
                        locationBetween(latitude, longitude),
                        priceBetweenOrMinMax(minPrice, maxPrice),
                        sizeBetweenOrMinMax(minSize, maxSize),
                        capacityBetweenOrMinMax(minCapacity, maxCapacity),
                        standBetweenOrMinMax(minStand, maxStand),
                        hasPiano(hasPiano),
                        hasAmp(hasAmp),
                        hasSpeaker(hasSpeaker),
                        hasMic(hasMic),
                        hasDrums(hasDrums));
        return PageableExecutionUtils.getPage(performanceHalls, pageable, () -> countQuery.fetchOne());
    }


    private BooleanExpression nameContains(String name){
        if(name == null || name.isEmpty()){
            return null;
        } else{
            return performanceHall.name.containsIgnoreCase(name);
        }
    }

    private BooleanExpression locationBetween(Double latitude, Double longitude){
        if(latitude == null || longitude == null){
            return null;
        } else{
            // [Haversine Formula]
            double range = 0.5; // 50km range
            return performanceHall.latitude.between(latitude - range, latitude + range)
                    .and(performanceHall.longitude.between(longitude - range, longitude + range));
        }
    }
    
    private BooleanExpression sizeBetweenOrMinMax(Integer minSize, Integer maxSize){
        if(minSize != null && maxSize != null){
            return performanceHall.size.between(minSize, maxSize);
        } else if(minSize != null){
            return performanceHall.size.goe(minSize);
        } else if(maxSize != null){
            return performanceHall.size.loe(maxSize);
        } else{
            return null;
        }
    }

    private BooleanExpression priceBetweenOrMinMax(Integer minPrice, Integer maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return performanceHall.price.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return performanceHall.price.goe(minPrice);
        } else if (maxPrice != null) {
            return performanceHall.price.loe(maxPrice);
        } else {
            return null;
        }
    }


    private BooleanExpression capacityBetweenOrMinMax(Integer minCapacity, Integer maxCapacity){
        if(minCapacity != null && maxCapacity != null){
            return performanceHall.capacity.between(minCapacity, maxCapacity);
        } else if(minCapacity != null){
            return performanceHall.capacity.goe(minCapacity);
        } else if(maxCapacity != null){
            return performanceHall.capacity.loe(maxCapacity);
        } else{
            return null;
        }
    }

    private BooleanExpression standBetweenOrMinMax(Integer minStand, Integer maxStand){
        if(minStand != null && maxStand != null){
            return performanceHall.stand.between(minStand, maxStand);
        } else if(minStand != null){
            return performanceHall.stand.goe(minStand);
        } else if(maxStand != null){
            return performanceHall.stand.loe(maxStand);
        } else{
            return null;
        }
    }
    private BooleanExpression hasPiano(Boolean hasPiano){
        if(hasPiano == null){
            return null;
        } else{
            return performanceHall.hasPiano.eq(hasPiano);
        }
    }

    private BooleanExpression hasAmp(Boolean hasAmp){
        if(hasAmp == null){
            return null;
        } else{
            return performanceHall.hasAmp.eq(hasAmp);
        }
    }

    private BooleanExpression hasSpeaker(Boolean hasSpeaker){
        if(hasSpeaker == null){
            return null;
        } else{
            return performanceHall.hasSpeaker.eq(hasSpeaker);
        }
    }

    private BooleanExpression hasMic(Boolean hasMic){
        if(hasMic == null){
            return null;
        } else{
            return performanceHall.hasMic.eq(hasMic);
        }
    }

    private BooleanExpression hasDrums(Boolean hasDrums){
        if(hasDrums == null){
            return null;
        } else{
            return performanceHall.hasDrums.eq(hasDrums);
        }
    }


    private OrderSpecifier<?> sortByIncludingDist(Sort sort, Double latitude, Double longitude) {
        Sort.Order order = sort.iterator().next();
        String property = order.getProperty();
        boolean isAscending = order.isAscending();

        switch (property) {
            case "distance":
                if (latitude == null || longitude == null) {
                    return null;
                }
                NumberExpression<Double> latitudeDiff = performanceHall.latitude.subtract(latitude).abs();
                NumberExpression<Double> longitudeDiff = performanceHall.longitude.subtract(longitude).abs();
                NumberExpression<Double> distance = latitudeDiff.add(longitudeDiff);
                return isAscending ? distance.asc() : distance.desc();
            case "price":
                return isAscending ? performanceHall.price.asc() : performanceHall.price.desc();
            case "size":
                return isAscending ? performanceHall.size.asc() : performanceHall.size.desc();
            case "capacity":
                return isAscending ? performanceHall.capacity.asc() : performanceHall.capacity.desc();
            case "stand":
                return isAscending ? performanceHall.stand.asc() : performanceHall.stand.desc();
            default:
                throw new CustomException(ErrorCode.INVALID_SORT_BY_FIELD);
        }
    }
}
