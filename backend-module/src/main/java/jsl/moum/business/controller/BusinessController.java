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
import org.springframework.data.domain.Sort;
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

    @GetMapping("/practice-rooms/search")
    public ResponseEntity<ResultResponse> searchPracticeRooms(@RequestParam(name = "page", defaultValue = "1") int page,
                                                              @RequestParam(name = "size", defaultValue = "10") int size,
                                                              @RequestParam(name = "sortBy", defaultValue = "distance") String sortBy,
                                                              @RequestParam(name = "orderBy", defaultValue = "asc") String orderBy,
                                                              @RequestParam(name = "name", required = false) String name,
                                                              @RequestParam(name = "address", required = false) String address,
                                                              @RequestParam(name = "latitude", required = false) Double latitude,
                                                              @RequestParam(name = "longitude", required = false) Double longitude,
                                                              @RequestParam(name = "minPrice", required = false) Integer minPrice,
                                                              @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
                                                              @RequestParam(name = "minCapacity", required = false) Integer minCapacity,
                                                              @RequestParam(name = "maxCapacity", required = false) Integer maxCapacity,
                                                              @RequestParam(name = "type", required = false) Integer type,
                                                              @RequestParam(name = "minStand", required = false) Integer minStand,
                                                              @RequestParam(name = "maxStand", required = false) Integer maxStand,
                                                              @RequestParam(name = "hasPiano", required = false) Boolean hasPiano,
                                                              @RequestParam(name = "hasAmp", required = false) Boolean hasAmp,
                                                              @RequestParam(name = "hasSpeaker", required = false) Boolean hasSpeaker,
                                                              @RequestParam(name = "hasMic", required = false) Boolean hasMic,
                                                              @RequestParam(name = "hasDrums", required = false) Boolean hasDrums){
        if(page < 1 || size < 1) {
            throw new CustomException(ErrorCode.INVALID_PAGE_VALUES);
        }

        Sort sort;
        if(orderBy.equalsIgnoreCase("desc")) {
            sort = Sort.by(Sort.Order.desc(sortBy));
        }else {
            sort = Sort.by(Sort.Order.asc(sortBy));
        }

        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        PracticeRoomDto.Search searchParams = PracticeRoomDto.Search.builder()
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minCapacity(minCapacity)
                .maxCapacity(maxCapacity)
                .type(type)
                .minStand(minStand)
                .maxStand(maxStand)
                .hasPiano(hasPiano)
                .hasAmp(hasAmp)
                .hasSpeaker(hasSpeaker)
                .hasMic(hasMic)
                .hasDrums(hasDrums)
                .build();

        Page<PracticeRoomDto.Response> practiceRoomsPage = businessService.searchPracticeRooms(pageRequest, searchParams);

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

    @GetMapping("/performance-halls/search")
    public ResponseEntity<ResultResponse> searchPerformanceHalls(@RequestParam(name = "page", defaultValue = "1") int page,
                                                                 @RequestParam(name = "size", defaultValue = "10") int size,
                                                                 @RequestParam(name = "sortBy", defaultValue = "distance") String sortBy,
                                                                 @RequestParam(name = "orderBy", defaultValue = "asc") String orderBy,
                                                                 @RequestParam(name = "name", required = false) String name,
                                                                 @RequestParam(name = "latitude", required = false) Double latitude,
                                                                 @RequestParam(name = "longitude", required = false) Double longitude,
                                                                 @RequestParam(name = "minPrice", required = false) Integer minPrice,
                                                                 @RequestParam(name = "maxPrice", required = false) Integer maxPrice,
                                                                 @RequestParam(name = "minHallSize", required = false) Integer minHallSize,
                                                                 @RequestParam(name = "maxHallSize", required = false) Integer maxHallSize,
                                                                 @RequestParam(name = "minCapacity", required = false) Integer minCapacity,
                                                                 @RequestParam(name = "maxCapacity", required = false) Integer maxCapacity,
                                                                 @RequestParam(name = "minStand", required = false) Integer minStand,
                                                                 @RequestParam(name = "maxStand", required = false) Integer maxStand,
                                                                 @RequestParam(name = "hasPiano", required = false) Boolean hasPiano,
                                                                 @RequestParam(name = "hasAmp", required = false) Boolean hasAmp,
                                                                 @RequestParam(name = "hasSpeaker", required = false) Boolean hasSpeaker,
                                                                 @RequestParam(name = "hasMic", required = false) Boolean hasMic,
                                                                 @RequestParam(name = "hasDrums", required = false) Boolean hasDrums){
        if(page < 1 || size < 1) {
            throw new CustomException(ErrorCode.INVALID_PAGE_VALUES);
        }

        Sort sort;
        if(orderBy.equalsIgnoreCase("desc")) {
            sort = Sort.by(Sort.Order.desc(sortBy));
        }else {
            sort = Sort.by(Sort.Order.asc(sortBy));
        }
        PageRequest pageRequest = PageRequest.of(page - 1, size, sort);
        PerformanceHallDto.Search searchParams = PerformanceHallDto.Search.builder()
                .name(name)
                .latitude(latitude)
                .longitude(longitude)
                .minPrice(minPrice)
                .maxPrice(maxPrice)
                .minSize(minHallSize)
                .maxSize(maxHallSize)
                .minCapacity(minCapacity)
                .maxCapacity(maxCapacity)
                .minStand(minStand)
                .maxStand(maxStand)
                .hasPiano(hasPiano)
                .hasAmp(hasAmp)
                .hasSpeaker(hasSpeaker)
                .hasMic(hasMic)
                .hasDrums(hasDrums)
                .build();

        Page<PerformanceHallDto.Response> performanceHallsPage = businessService.searchPerformanceHalls(pageRequest, searchParams);

        ResultResponse result = ResultResponse.of(ResponseCode.GET_PERFORMANCE_HALL_SUCCESS, performanceHallsPage);
        return ResponseEntity.ok(result);
    }

}
