package jsl.moum.community.likes.controller;

import jsl.moum.auth.domain.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jsl.moum.community.likes.dto.LikesDto;
import jsl.moum.community.likes.service.LikesService;
import jsl.moum.global.error.exception.NeedLoginException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;

@RestController
@RequiredArgsConstructor
public class LikesController {

    private final LikesService likesService;

    /**
     * 좋아요 등록 API
     */
    @PostMapping("/api/likes/{articleId}")
    public ResponseEntity<ResultResponse> createLikes(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @PathVariable int articleId){
        if(customUserDetails == null){
            throw new NeedLoginException();
        }
        LikesDto.Response likesResponse = likesService.createLikes(customUserDetails.getUsername(),articleId);

        ResultResponse response = ResultResponse.of(ResponseCode.LIKES_CREATE_SUCCESS, likesResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 좋아요 삭제 API
     */
    @DeleteMapping("/api/likes/{articleId}")
    public ResponseEntity<ResultResponse> deleteLikes(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @PathVariable int articleId){
        if(customUserDetails == null){
            throw new NeedLoginException();
        }

        LikesDto.Response likesResponse = likesService.deleteLikes(articleId,customUserDetails.getUsername());

        ResultResponse response = ResultResponse.of(ResponseCode.LIKES_DELETE_SUCCESS, likesResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
}
