package jsl.moum.backendmodule.community.profile.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.backendmodule.auth.domain.CustomUserDetails;
import jsl.moum.backendmodule.community.profile.dto.ProfileDto;
import jsl.moum.backendmodule.community.profile.service.ProfileService;
import jsl.moum.backendmodule.global.error.exception.NeedLoginException;
import jsl.moum.backendmodule.global.response.ResponseCode;
import jsl.moum.backendmodule.global.response.ResultResponse;

import java.io.IOException;

@RestController
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    /**
     프로필 조회
     */
    @GetMapping("/api/profiles/{memberId}")
    public ResponseEntity<ResultResponse> getProfileById(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                         @PathVariable int memberId){

        loginCheck(customUserDetails.getUsername());
        ProfileDto.Response responseDto = profileService.getProfile(memberId);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_PROFILE_SUCCESS, responseDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    /**
     프로필 수정
     */
    @PatchMapping("/api/profiles/{memberId}")
    public ResponseEntity<ResultResponse> updateProfileById(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                            @PathVariable int memberId,
                                                            @Valid @RequestPart(value = "updateProfileDto") ProfileDto.UpdateRequest profileUpdateDto,
                                                            @RequestPart(value = "file", required = false) MultipartFile file) throws IOException {

        String loginUserName = loginCheck(customUserDetails.getUsername());
        ProfileDto.Response responseDto = profileService.updateProfile(loginUserName, memberId, profileUpdateDto, file);
        ResultResponse response = ResultResponse.of(ResponseCode.UPDATE_PROFILE_SUCCESS, responseDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    public String loginCheck(String username){
        if(username == null){
            throw new NeedLoginException();
        }

        return username;
    }
}
