package jsl.moum.business.service;

import jsl.moum.business.domain.PerformanceHall;
import jsl.moum.business.domain.PerformanceHallRepository;
import jsl.moum.business.domain.PracticeRoom;
import jsl.moum.business.domain.PracticeRoomRepository;
import jsl.moum.business.dto.PerformanceHallDto;
import jsl.moum.business.dto.PracticeRoomDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

@Service
@Slf4j
@RequiredArgsConstructor
public class BusinessService {

    private final PracticeRoomRepository practiceRoomRepository;
    private final PerformanceHallRepository performanceHallRepository;

    public PracticeRoomDto getPracticeRoomById(int id){
        PracticeRoom room = practiceRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 연습실 정보가 존재하지 않습니다."));
        return new PracticeRoomDto(room);
    }

    public PerformanceHallDto getPerformanceHallById(int id){
        PerformanceHall hall = performanceHallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공연장 정보가 존재하지 않습니다."));
        return new PerformanceHallDto(hall);
    }
}
