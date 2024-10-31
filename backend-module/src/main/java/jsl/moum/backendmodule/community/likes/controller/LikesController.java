package jsl.moum.backendmodule.community.likes.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import jsl.moum.backendmodule.auth.domain.CustomUserDetails;
import jsl.moum.backendmodule.community.likes.dto.LikesDto;
import jsl.moum.backendmodule.community.likes.service.LikesService;
import jsl.moum.backendmodule.global.error.exception.NeedLoginException;
import jsl.moum.backendmodule.global.response.ResponseCode;
import jsl.moum.backendmodule.global.response.ResultResponse;

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
