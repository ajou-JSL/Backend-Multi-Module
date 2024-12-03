package jsl.moum.business.service;

import jsl.moum.business.domain.PerformanceHall;
import jsl.moum.business.domain.PerformanceHallRepository;
import jsl.moum.business.domain.PracticeRoom;
import jsl.moum.business.domain.PracticeRoomRepository;
import jsl.moum.business.dto.PerformanceHallDto;
import jsl.moum.business.dto.PracticeRoomDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@Slf4j
@RequiredArgsConstructor
public class BusinessService {

    private final PracticeRoomRepository practiceRoomRepository;
    private final PerformanceHallRepository performanceHallRepository;

    /**
     *
     * Practice Rooms Methods
     *
     */

    public PracticeRoomDto getPracticeRoomById(int id){
        PracticeRoom room = practiceRoomRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PRACTICE_ROOM_NOT_FOUND));
        return new PracticeRoomDto(room);
    }

    public Page<PracticeRoomDto.Response> getPracticeRoomsPaged(PageRequest pageRequest){
        return practiceRoomRepository.findAll(pageRequest).map(PracticeRoomDto.Response::new);
    }

    public Page<PracticeRoomDto.Response> searchPracticeRooms(PageRequest pageRequest, PracticeRoomDto.Search searchParams){
        return practiceRoomRepository.findAllBySearchParams(pageRequest, searchParams).map(PracticeRoomDto.Response::new);
    }

    /**
     *
     * Performance Halls Methods
     *
     */

    public PerformanceHallDto getPerformanceHallById(int id){
        PerformanceHall hall = performanceHallRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.PERFORMANCE_HALL_NOT_FOUND));
        return new PerformanceHallDto(hall);
    }

    public Page<PerformanceHallDto.Response> getPerformanceHallsPaged(PageRequest pageRequest){
        return performanceHallRepository.findAll(pageRequest).map(PerformanceHallDto.Response::new);
    }

    public Page<PerformanceHallDto.Response> searchPerformanceHalls(PageRequest pageRequest, PerformanceHallDto.Search searchParams){
        return performanceHallRepository.findAllBySearchParams(pageRequest, searchParams).map(PerformanceHallDto.Response::new);
    }

}
