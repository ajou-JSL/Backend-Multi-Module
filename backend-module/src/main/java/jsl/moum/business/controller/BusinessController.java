package jsl.moum.business.controller;

import jsl.moum.business.dto.PerformanceHallDto;
import jsl.moum.business.dto.PracticeRoomDto;
import jsl.moum.business.service.BusinessService;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/business")
@Slf4j
public class BusinessController {

    private final BusinessService businessService;

    @GetMapping("/practice-room/view/{id}")
    @ResponseBody
    public ResponseEntity<ResultResponse> getPracticeRoomDetails(@PathVariable(name = "id") int id) {
        PracticeRoomDto practiceRoomDto = businessService.getPracticeRoomById(id);
        ResultResponse result = ResultResponse.of(ResponseCode.GET_PRACTICE_ROOM_SUCCESS, practiceRoomDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/practice-rooms")
    public ResponseEntity<ResultResponse> getPracticeRooms(@RequestParam(name = "page", defaultValue = "1") int page,
                                                           @RequestParam(name = "size", defaultValue = "10") int size) {
        if(page < 1 || size < 1) {
            throw new CustomException(ErrorCode.INVALID_PAGE_VALUES);
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<PracticeRoomDto.Response> practiceRoomsPage = businessService.getPracticeRoomsPaged(pageRequest);

        ResultResponse result = ResultResponse.of(ResponseCode.GET_PRACTICE_ROOM_SUCCESS, practiceRoomsPage);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/performance-hall/view/{id}")
    @ResponseBody
    public ResponseEntity<ResultResponse> getPerformanceHallDetails(@PathVariable(name = "id") int id) {
        PerformanceHallDto performanceHallDto = businessService.getPerformanceHallById(id);
        ResultResponse result = ResultResponse.of(ResponseCode.GET_PERFORMANCE_HALL_SUCCESS, performanceHallDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/performance-halls")
    public ResponseEntity<ResultResponse> getPerformanceHalls(@RequestParam(name = "page", defaultValue = "1") int page,
                                                              @RequestParam(name = "size", defaultValue = "10") int size) {
        if(page < 1 || size < 1) {
            throw new CustomException(ErrorCode.INVALID_PAGE_VALUES);
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size);
        Page<PerformanceHallDto.Response> performanceHallsPage = businessService.getPerformanceHallsPaged(pageRequest);

        ResultResponse result = ResultResponse.of(ResponseCode.GET_PERFORMANCE_HALL_SUCCESS, performanceHallsPage);
        return ResponseEntity.ok(result);
    }
}
