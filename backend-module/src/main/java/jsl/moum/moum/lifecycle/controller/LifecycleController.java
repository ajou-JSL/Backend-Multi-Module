package jsl.moum.moum.lifecycle.controller;

import jakarta.validation.Valid;
import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.error.exception.NeedLoginException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.moum.lifecycle.dto.LifecyclePerformanceHallDto;
import jsl.moum.moum.lifecycle.dto.LifecyclePracticeRoomDto;
import jsl.moum.moum.lifecycle.dto.LifecycleDto;
import jsl.moum.moum.lifecycle.dto.ProcessDto;
import jsl.moum.moum.lifecycle.service.LifecycleService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequiredArgsConstructor
public class LifecycleController {

    private final LifecycleService lifecycleService;

    /**
     * 모음 조회
     */
    @GetMapping("/api/moum/{moumId}")
    public ResponseEntity<ResultResponse> getMoumById(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                       @PathVariable int moumId)
    {
        String username = loginCheck(customUserDetails.getUsername());
        LifecycleDto.Response responseDto = lifecycleService.getMoumById(username, moumId);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_MOUM_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 나의 모음 리스트 조회
     */
    @GetMapping("/api/moum-all/my")
    public ResponseEntity<ResultResponse> getMyMoumList(@AuthenticationPrincipal CustomUserDetails customUserDetails)
    {

        String username = loginCheck(customUserDetails.getUsername());
        List<LifecycleDto.Response> responseDto = lifecycleService.getMyMoumList(username);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_MOUM_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 팀의 모음 리스트 조회
     */
    @GetMapping("/api/moum-all/team/{teamId}")
    public ResponseEntity<ResultResponse> getTeamMoumList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                          @PathVariable int teamId)
    {
        loginCheck(customUserDetails.getUsername());
        List<LifecycleDto.Response> responseDto = lifecycleService.getTeamMoumList(teamId);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_MOUM_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 모음 생성
     */
    @PostMapping("/api/moum")
    public ResponseEntity<ResultResponse> addMoum(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                       @Valid @RequestPart(name = "lifecycleRequestDto") LifecycleDto.Request lifecycleRequestDto,
                                                       @RequestPart(value = "file", required = false)List<MultipartFile> files) throws IOException {
        String username = loginCheck(customUserDetails.getUsername());
        LifecycleDto.Response responseDto = lifecycleService.addMoum(username, lifecycleRequestDto, files);
        ResultResponse response = ResultResponse.of(ResponseCode.CREATE_MOUM_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 모음 정보 수정
     */
    @PatchMapping("/api/moum/{moumId}")
    public ResponseEntity<ResultResponse> updateMoum(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                  @Valid @RequestPart(name = "lifecycleRequestDto") LifecycleDto.Request lifecycleRequestDto,
                                                  @RequestPart(value = "file", required = false)List<MultipartFile> files,
                                                  @PathVariable int moumId) throws IOException {
        String username = loginCheck(customUserDetails.getUsername());
        LifecycleDto.Response responseDto = lifecycleService.updateMoum(username, lifecycleRequestDto, files, moumId);
        ResultResponse response = ResultResponse.of(ResponseCode.UPDATE_MOUM_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 모음 삭제
     */
    @DeleteMapping("/api/moum/{moumId}")
    public ResponseEntity<ResultResponse> deleteMoum(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @PathVariable int moumId)
    {
        String username = loginCheck(customUserDetails.getUsername());
        LifecycleDto.Response responseDto = lifecycleService.deleteMoum(username,moumId);
        ResultResponse response = ResultResponse.of(ResponseCode.DELETE_MOUM_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }


    /**
     * 모음 마감하기
     */
    @PatchMapping("/api/moum/finish/{moumId}")
    public ResponseEntity<ResultResponse> finishMoum(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @PathVariable int moumId){
        String username = loginCheck(customUserDetails.getUsername());
        LifecycleDto.Response responseDto = lifecycleService.finishMoum(username,moumId);
        ResultResponse response = ResultResponse.of(ResponseCode.FINISH_MOUM_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 모음 되살리기
     */
    @PatchMapping("/api/moum/reopen/{moumId}")
    public ResponseEntity<ResultResponse> reopenMoum(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @PathVariable int moumId)
    {
        String username = loginCheck(customUserDetails.getUsername());
        LifecycleDto.Response responseDto = lifecycleService.reopenMoum(username, moumId);
        ResultResponse response = ResultResponse.of(ResponseCode.REOPEN_MOUM_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 모음 진척도 수정하기
     */
    @PatchMapping("/api/moum/update-process/{moumId}")
    public ResponseEntity<ResultResponse> updateMoumProcess(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @PathVariable int moumId, @RequestBody ProcessDto processDto)
    {
        String username = loginCheck(customUserDetails.getUsername());
        LifecycleDto.Response responseDto = lifecycleService.updateProcessStatus(username, moumId,processDto);
        ResultResponse response = ResultResponse.of(ResponseCode.UPDATE_MOUM_PROCESS_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 모음 연습실 & 공연장 추가
     */

    @PostMapping("/api/moum/practice-room")
    public ResponseEntity<ResultResponse> addPracticeRoom(@RequestBody LifecyclePracticeRoomDto.Request requestDto){
        LifecyclePracticeRoomDto.Response responseDto = lifecycleService.addPracticeRoom(requestDto);
        ResultResponse response = ResultResponse.of(ResponseCode.MOUM_ADD_PRACTICE_ROOM_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("/api/moum/{id}/practice-room")
    public ResponseEntity<ResultResponse> getPracticeRooms(@PathVariable(name = "id") int id){
        List<LifecyclePracticeRoomDto.Response> responseDto = lifecycleService.getPracticeRooms(id);

        if (responseDto == null || responseDto.isEmpty()) {
            throw new CustomException(ErrorCode.MOUM_PRACTICE_ROOM_NOT_FOUND);
        }

        ResultResponse response = ResultResponse.of(ResponseCode.MOUM_GET_PRACTICE_ROOM_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @DeleteMapping("/api/moum/{id}/practice-room")
    public ResponseEntity<ResultResponse> deletePracticeRooms(@PathVariable(name = "id") int id){
        lifecycleService.deletePracticeRooms(id);
        ResultResponse response = ResultResponse.of(ResponseCode.MOUM_DELETE_PRACTICE_ROOM_SUCCESS,null);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @DeleteMapping("/api/moum/{moumId}/practice-room/{id}")
    public ResponseEntity<ResultResponse> deletePracticeRoom(@PathVariable(name = "moumId") int moumId,
                                                             @PathVariable(name = "id") int id){
        lifecycleService.deletePracticeRoom(id, moumId);
        ResultResponse response = ResultResponse.of(ResponseCode.MOUM_DELETE_PRACTICE_ROOM_SUCCESS,null);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }



    @PostMapping("/api/moum/performance-hall")
    public ResponseEntity<ResultResponse> addPerformanceHall(@RequestBody LifecyclePerformanceHallDto.Request request){
        LifecyclePerformanceHallDto.Response responseDto = lifecycleService.addPerformanceHall(request);
        ResultResponse response = ResultResponse.of(ResponseCode.MOUM_ADD_PERFORMANCE_HALL_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("/api/moum/{id}/performance-hall")
    public ResponseEntity<ResultResponse> getPerformanceHalls(@PathVariable(name = "id") int id){
        List<LifecyclePerformanceHallDto.Response> responseDto = lifecycleService.getPerformanceHalls(id);

        if (responseDto == null || responseDto.isEmpty()) {
            throw new CustomException(ErrorCode.MOUM_PERFORMANCE_HALL_NOT_FOUND);
        }

        ResultResponse response = ResultResponse.of(ResponseCode.MOUM_GET_PERFORMANCE_HALL_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @DeleteMapping("/api/moum/{id}/performance-hall")
    public ResponseEntity<ResultResponse> deletePerformanceHalls(@PathVariable(name = "id") int id){
        lifecycleService.deletePerformanceHalls(id);
        ResultResponse response = ResultResponse.of(ResponseCode.MOUM_DELETE_PERFORMANCE_HALL_SUCCESS,null);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @DeleteMapping("/api/moum/{moumId}/performance-hall/{id}")
    public ResponseEntity<ResultResponse> deletePerformanceHall(@PathVariable(name = "moumId") int moumId,
                                                                @PathVariable(name = "id") int id){
        lifecycleService.deletePerformanceHall(id, moumId);
        ResultResponse response = ResultResponse.of(ResponseCode.MOUM_DELETE_PERFORMANCE_HALL_SUCCESS,null);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }


    public String loginCheck(String username){
        if(username == null){
            throw new NeedLoginException();
        }

        return username;
    }

}
