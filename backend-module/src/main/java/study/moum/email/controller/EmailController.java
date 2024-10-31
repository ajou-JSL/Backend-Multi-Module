package study.moum.email.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import study.moum.email.dto.EmailDto;
import study.moum.email.dto.VerifyDto;
import study.moum.email.service.EmailService;
import study.moum.global.error.ErrorCode;
import study.moum.global.error.ErrorResponse;
import study.moum.global.response.ResponseCode;
import study.moum.global.response.ResultResponse;

@RestController
@RequiredArgsConstructor
public class EmailController {
    private final EmailService emailService;

    @PostMapping("/send-mail")
    public ResponseEntity<?> emailAuthentification (@Valid @RequestBody EmailDto.Request emailRequestDto) throws Exception {
        String verifyCode = emailService.sendCertificationMail(emailRequestDto);

        ResultResponse resultResponse = ResultResponse.of(ResponseCode.EMAIL_SEND_SUCCESS, /*verifyCode*/emailRequestDto.getEmail());
        return new ResponseEntity<>(resultResponse, HttpStatus.valueOf(resultResponse.getStatus()));
    }

    @PostMapping("/verify-code")
    public ResponseEntity<?> verifyCode(@Valid @RequestBody VerifyDto.Request verifyDto) {

        VerifyDto.Response resultDto = emailService.verifyCode(verifyDto);

        if (resultDto.getResult()) {
            ResultResponse result = ResultResponse.of(ResponseCode.EMAIL_VERIFY_SUCCESS, null);
            return new ResponseEntity<>(result, HttpStatus.valueOf(result.getStatus()));
        }
        else {
            ErrorResponse result = ErrorResponse.of(ErrorCode.EMAIL_VERIFY_FAILED);
            return new ResponseEntity<>(result,HttpStatus.valueOf(result.getStatus()));
        }
    }
}
