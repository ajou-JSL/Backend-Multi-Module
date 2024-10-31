package study.moum.auth.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import study.moum.auth.dto.MemberDto;
import study.moum.auth.service.SignupService;
import study.moum.global.response.ResponseCode;
import study.moum.global.response.ResultResponse;

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
