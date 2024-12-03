package jsl.moum.business.domain;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.business.dto.PracticeRoomDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.support.PageableExecutionUtils;

import java.util.List;

import static jsl.moum.business.domain.QPracticeRoom.*;

@RequiredArgsConstructor
public class PracticeRoomRepositoryImpl implements PracticeRoomRepositoryCustom {



    private final JPAQueryFactory queryFactory;

    @Override
    public Page<PracticeRoom> findAllBySearchParams(Pageable pageable, PracticeRoomDto.Search searchParams) {

        String name = searchParams.getName();
        Double latitude = searchParams.getLatitude();
        Double longitude = searchParams.getLongitude();
        Integer minPrice = searchParams.getMinPrice();
        Integer maxPrice = searchParams.getMaxPrice();
        Integer minCapacity = searchParams.getMinCapacity();
        Integer maxCapacity = searchParams.getMaxCapacity();
        Integer type = searchParams.getType();
        Integer minStand = searchParams.getMinStand();
        Integer maxStand = searchParams.getMaxStand();
        Boolean hasPiano = searchParams.getHasPiano();
        Boolean hasAmp = searchParams.getHasAmp();
        Boolean hasSpeaker = searchParams.getHasSpeaker();
        Boolean hasMic = searchParams.getHasMic();
        Boolean hasDrums = searchParams.getHasDrums();

        OrderSpecifier<?> sortOrderSpecifier = sortByIncludingDist(pageable.getSort(), latitude, longitude);

        List<PracticeRoom> practiceRooms = queryFactory.selectFrom(practiceRoom)
                .where(nameContains(name),
                        locationBetween(latitude, longitude),
                        priceBetweenOrMinMax(minPrice, maxPrice),
                        capacityBetweenOrMinMax(minCapacity, maxCapacity),
                        typeEq(type),
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
                .select(practiceRoom.count())
                .from(practiceRoom)
                .where(nameContains(name),
                        locationBetween(latitude, longitude),
                        priceBetweenOrMinMax(minPrice, maxPrice),
                        capacityBetweenOrMinMax(minCapacity, maxCapacity),
                        typeEq(type),
                        standBetweenOrMinMax(minStand, maxStand),
                        hasPiano(hasPiano),
                        hasAmp(hasAmp),
                        hasSpeaker(hasSpeaker),
                        hasMic(hasMic),
                        hasDrums(hasDrums));
        return PageableExecutionUtils.getPage(practiceRooms, pageable, () -> countQuery.fetchOne());
    }

    private BooleanExpression nameContains(String name){
        if(name == null || name.isEmpty()){
            return null;
        } else{
            return practiceRoom.name.containsIgnoreCase(name);
        }
    }

    private BooleanExpression locationBetween(Double latitude, Double longitude){
        if(latitude == null || longitude == null){
            return null;
        } else{
            // [Haversine Formula]
            double range = 0.5; // 50km range
            return practiceRoom.latitude.between(latitude - range, latitude + range)
                .and(practiceRoom.longitude.between(longitude - range, longitude + range));
        }
    }

    private BooleanExpression priceBetweenOrMinMax(Integer minPrice, Integer maxPrice) {
        if (minPrice != null && maxPrice != null) {
            return practiceRoom.price.between(minPrice, maxPrice);
        } else if (minPrice != null) {
            return practiceRoom.price.goe(minPrice);
        } else if (maxPrice != null) {
            return practiceRoom.price.loe(maxPrice);
        } else {
            return null;
        }
    }


    private BooleanExpression capacityBetweenOrMinMax(Integer minCapacity, Integer maxCapacity){
        if(minCapacity != null && maxCapacity != null){
            return practiceRoom.capacity.between(minCapacity, maxCapacity);
        } else if(minCapacity != null){
            return practiceRoom.capacity.goe(minCapacity);
        } else if(maxCapacity != null){
            return practiceRoom.capacity.loe(maxCapacity);
        } else{
            return null;
        }
    }

    private BooleanExpression typeEq(Integer type){
        if(type == null){
            return null;
        } else{
            return practiceRoom.type.eq(type);
        }
    }

    private BooleanExpression standBetweenOrMinMax(Integer minStand, Integer maxStand){
        if(minStand != null && maxStand != null){
            return practiceRoom.stand.between(minStand, maxStand);
        } else if(minStand != null){
            return practiceRoom.stand.goe(minStand);
        } else if(maxStand != null){
            return practiceRoom.stand.loe(maxStand);
        } else{
            return null;
        }
    }

    private BooleanExpression hasPiano(Boolean hasPiano){
        if(hasPiano == null){
            return null;
        } else{
            return practiceRoom.hasPiano.eq(hasPiano);
        }
    }

    private BooleanExpression hasAmp(Boolean hasAmp){
        if(hasAmp == null){
            return null;
        } else{
            return practiceRoom.hasAmp.eq(hasAmp);
        }
    }

    private BooleanExpression hasSpeaker(Boolean hasSpeaker){
        if(hasSpeaker == null){
            return null;
        } else{
            return practiceRoom.hasSpeaker.eq(hasSpeaker);
        }
    }

    private BooleanExpression hasMic(Boolean hasMic){
        if(hasMic == null){
            return null;
        } else{
            return practiceRoom.hasMic.eq(hasMic);
        }
    }

    private BooleanExpression hasDrums(Boolean hasDrums){
        if(hasDrums == null){
            return null;
        } else{
            return practiceRoom.hasDrums.eq(hasDrums);
        }
    }

    private OrderSpecifier<?> sortByIncludingDist(Sort sort, Double latitude, Double longitude) {
        Sort.Order order = sort.iterator().next();
        String property = order.getProperty();
        boolean isAscending = order.isAscending();

        switch(property){
            case "distance":
                if(latitude == null || longitude == null){
                    return null;
                }
                NumberExpression<Double> latitudeDiff = practiceRoom.latitude.subtract(latitude).abs();
                NumberExpression<Double> longitudeDiff = practiceRoom.longitude.subtract(longitude).abs();
                NumberExpression<Double> distance = latitudeDiff.add(longitudeDiff);
                return isAscending ? distance.asc() : distance.desc();
            case "price":
                return isAscending ? practiceRoom.price.asc() : practiceRoom.price.desc();
            case "capacity":
                return isAscending ? practiceRoom.capacity.asc() : practiceRoom.capacity.desc();
            case "stand":
                return isAscending ? practiceRoom.stand.asc() : practiceRoom.stand.desc();
            default:
                throw new CustomException(ErrorCode.INVALID_SORT_BY_FIELD);
        }
    }

}
