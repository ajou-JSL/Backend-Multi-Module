package jsl.moum.member_profile.controller;

import jakarta.validation.Valid;
import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.auth.dto.MemberSortDto;
import jsl.moum.member_profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.member_profile.dto.ProfileDto;
import jsl.moum.global.error.exception.NeedLoginException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;

import java.io.IOException;
import java.util.List;

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

    /**
     랭킹(exp) 순 멤버 리스트 조회
     */
    @GetMapping("/api/profiles-all/rank")
    public ResponseEntity<ResultResponse> getProfilesSortByExp(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){

        loginCheck(customUserDetails.getUsername());
        Page<MemberSortDto.ExpResponse> responseDto = profileService.getProfilesSortByExp(page, size);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_PROFILE_SUCCESS, responseDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    /**
     이력 개수 순 멤버 리스트 조회
     */
    @GetMapping("/api/profiles-all/records")
    public ResponseEntity<ResultResponse> getProfilesSortByRecordsCount(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size){

        loginCheck(customUserDetails.getUsername());
        List<MemberSortDto.RecordsCountResponse> responseDto = profileService.getProfilesSortByRecordsCount(page, size);
        ResultResponse response = ResultResponse.of(ResponseCode.GET_PROFILE_SUCCESS, responseDto);
        return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
    }

    public String loginCheck(String username){
        if(username == null){
            throw new NeedLoginException();
        }

        return username;
    }
}
