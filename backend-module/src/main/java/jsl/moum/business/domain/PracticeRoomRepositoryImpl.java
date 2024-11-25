package jsl.moum.business.domain;

import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.business.dto.PracticeRoomDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

//        OrderSpecifier<?> orderSpecifier = ;

        /**
         *
         * TODO
         *
         */

        List<PracticeRoom> practiceRooms = queryFactory.selectFrom(practiceRoom)
                .where(nameContains(name),
                        nearestLocation(latitude, longitude), // TODO // Maybe OrderSpecifier???
                        priceBetween(minPrice, maxPrice),
                        capacityBetween(minCapacity, maxCapacity),
                        typeEq(type),
                        standBetween(minStand, maxStand),
                        hasPiano(hasPiano),
                        hasAmp(hasAmp),
                        hasSpeaker(hasSpeaker),
                        hasMic(hasMic),
                        hasDrums(hasDrums))
                .orderBy() // Implement sortBy(sortBy, ascending)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Separate COUNT query for efficiency
        JPAQuery<Long> countQuery = queryFactory
                .select(practiceRoom.count())
                .from(practiceRoom)
                .where(nameContains(name),
                        nearestLocation(latitude, longitude),
                        priceBetween(minPrice, maxPrice),
                        capacityBetween(minCapacity, maxCapacity),
                        typeEq(type),
                        standBetween(minStand, maxStand),
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

    private BooleanExpression nearestLocation(Double latitude, Double longitude){
        if(latitude == null || longitude == null){
            return null;
        } else{

            /**
             *
             * TODO
             * Sort by lowest value of Math.abs(roomLatitude - latitude) + Math.abs(roomLongitude - longitude)
             */

            /* [Haversine Formula]
            double range = 0.1; // Adjust the range based on desired proximity
            return practiceRoom.latitude.between(latitude - range, latitude + range)
                .and(practiceRoom.longitude.between(longitude - range, longitude + range));
             */

            return practiceRoom.latitude.eq(latitude).and(practiceRoom.longitude.eq(longitude));
        }
    }

    private BooleanExpression priceBetween(Integer minPrice, Integer maxPrice) {
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


    private BooleanExpression capacityBetween(Integer minCapacity, Integer maxCapacity){
        if(minCapacity == null || maxCapacity == null){
            return null;
        } else{
            return practiceRoom.capacity.between(minCapacity, maxCapacity);
        }
    }

    private BooleanExpression typeEq(Integer type){
        if(type == null){
            return null;
        } else{
            return practiceRoom.type.eq(type);
        }
    }

    private BooleanExpression standBetween(Integer minStand, Integer maxStand){
        if(minStand == null || maxStand == null){
            return null;
        } else{
            return practiceRoom.stand.between(minStand, maxStand);
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

//    private OrderSpecifier<?> sortBy(String sortBy, boolean ascending) {
//        PathBuilder<PracticeRoom> path = new PathBuilder<>(PracticeRoom.class, "practiceRoom");
//        return ascending ? path.get(sortBy).asc() : path.get(sortBy).desc();
//    }

}
