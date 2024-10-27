package jsl.moum.backendmodule.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.backendmodule.auth.dto.MemberDto;
import jsl.moum.backendmodule.auth.service.SignupService;
import jsl.moum.backendmodule.global.response.ResponseCode;
import jsl.moum.backendmodule.global.response.ResultResponse;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class SignupController {
    private final SignupService signupService;

    @PostMapping("/join")
    public ResponseEntity<ResultResponse> signupMember(@Valid @RequestPart MemberDto.Request memberRequestDto,
                                                       @RequestPart(value = "profileImage", required = false)MultipartFile file) throws IOException {

        signupService.signupMember(memberRequestDto, file);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.REGISTER_SUCCESS, memberRequestDto.getUsername());

        return new ResponseEntity<>(resultResponse, HttpStatus.valueOf(resultResponse.getStatus()));
    }
}
