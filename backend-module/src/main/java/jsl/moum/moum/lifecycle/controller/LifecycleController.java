package jsl.moum.moum.lifecycle.controller;

import jakarta.validation.Valid;
import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.global.error.exception.NeedLoginException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.moum.lifecycle.dto.LifecycleDto;
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
                                                       @Valid @RequestPart LifecycleDto.Request lifecycleRequestDto,
                                                       @RequestPart(value = "file", required = false)MultipartFile file) throws IOException {
        String username = loginCheck(customUserDetails.getUsername());
        LifecycleDto.Response responseDto = lifecycleService.addMoum(username, lifecycleRequestDto, file);
        ResultResponse response = ResultResponse.of(ResponseCode.CREATE_MOUM_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 모음 정보 수정
     */
    @PatchMapping("/api/moum/{moumId}")
    public ResponseEntity<ResultResponse> updateMoum(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                  @Valid @RequestPart LifecycleDto.Request lifecycleRequestDto,
                                                  @RequestPart(value = "file", required = false)MultipartFile file,
                                                  @PathVariable int moumId)
    {
        String username = loginCheck(customUserDetails.getUsername());
        LifecycleDto.Response responseDto = lifecycleService.updateMoum(username, lifecycleRequestDto, file, moumId);
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
     * todo : 선택하면 진행률 관련한거 업데이트되는 로직 필요함
     */



    public String loginCheck(String username){
        if(username == null){
            throw new NeedLoginException();
        }

        return username;
    }

}
