package jsl.moum.auth.controller;

import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.auth.service.SignoutService;
import jsl.moum.common.CommonService;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class SignoutController {
    private final SignoutService signoutService;
    private final CommonService commonService;

    @PatchMapping("/api/signout")
    public ResponseEntity<ResultResponse> signout(@AuthenticationPrincipal CustomUserDetails customUserDetails){

        String username = commonService.loginCheck(customUserDetails.getUsername());
        MemberDto.Response responseDto = signoutService.signoutMember(username);
        ResultResponse resultResponse = ResultResponse.of(ResponseCode.SIGN_OUT_SUCCESS,responseDto);
        return new ResponseEntity<>(resultResponse, HttpStatus.valueOf(resultResponse.getStatus()));
    }
}
