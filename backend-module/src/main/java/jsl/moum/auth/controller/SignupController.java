package jsl.moum.auth.controller;

import jakarta.validation.Valid;
import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.auth.service.SignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SignupController {
    private final SignupService signupService;

    @PostMapping("/join")
    public ResponseEntity<ResultResponse> signupMember(@Valid @RequestPart MemberDto.Request memberRequestDto,
                                                       @RequestPart(value = "profileImage", required = false)MultipartFile file) throws IOException {

        String resultString = signupService.signupMember(memberRequestDto, file);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.REGISTER_SUCCESS, resultString);

        return new ResponseEntity<>(resultResponse, HttpStatus.valueOf(resultResponse.getStatus()));
    }

    @PatchMapping("/re-join")
    public ResponseEntity<ResultResponse> reJoin(@RequestBody MemberDto.RejoinRequest rejoinRequestDto){

        MemberDto.Response responseDto = signupService.rejoinMember(rejoinRequestDto.getUsername());
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.REJOIN_SUCCESS,responseDto);
        return new ResponseEntity<>(resultResponse, HttpStatus.valueOf(resultResponse.getStatus()));
    }
}
