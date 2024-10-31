package jsl.moum.backendmodule.community.record.controller;

import jakarta.validation.Valid;
import jsl.moum.backendmodule.auth.domain.CustomUserDetails;
import jsl.moum.backendmodule.community.record.dto.RecordDto;
import jsl.moum.backendmodule.community.record.service.RecordService;
import jsl.moum.backendmodule.global.error.exception.NeedLoginException;
import jsl.moum.backendmodule.global.response.ResponseCode;
import jsl.moum.backendmodule.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecordController {

    private final RecordService recordService;


    /**
     이력 추가(생성)
     */
    @PostMapping("/api/records/{memberId}")
    public ResponseEntity<ResultResponse> addRecords(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                     @PathVariable int memberId,
                                                     @Valid @RequestBody RecordDto.Request requestDto){

        loginCheck(customUserDetails.getUsername());
        RecordDto.Response responseDto = recordService.addRecord(memberId, requestDto);
        ResultResponse response = ResultResponse.of(ResponseCode.RECORD_ADD_SUCCESS,responseDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));

    }

    /**
     todo : 이력 삭제
     */

    // todo : refactoring -> 중복 메소드
    public String loginCheck(String username){
        if(username == null){
            throw new NeedLoginException();
        }

        return username;
    }
}
