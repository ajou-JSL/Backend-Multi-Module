package jsl.moum.community.likes.controller;

import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.parameters.P;
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
    private final MemberRepository memberRepository;

    @GetMapping("/api/articles/member/{memberId}/likes/{articleId}")
    public ResponseEntity<ResultResponse> getLikes(@PathVariable(name = "memberId") int memberId, @PathVariable(name = "articleId") int articleId){
        LikesDto.Toggle isLikedArticle = likesService.isMemberLikesArticle(memberId, articleId);
        ResultResponse response = ResultResponse.of(ResponseCode.LIKES_GET_SUCCESS, isLikedArticle);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("/api/performs/member/{memberId}/likes/{performArticleId}")
    public ResponseEntity<ResultResponse> getPerformLikes(@PathVariable(name = "memberId") int memberId, @PathVariable(name = "performArticleId") int performArticleId){
        LikesDto.Toggle isLikedPerformArticle = likesService.isMemberLikesPerformArticle(memberId, performArticleId);
        ResultResponse response = ResultResponse.of(ResponseCode.LIKES_GET_SUCCESS, isLikedPerformArticle);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @PutMapping("/api/articles/member/{memberId}/likes/{articleId}")
    public ResponseEntity<ResultResponse> createLikes(@PathVariable(name = "memberId") int memberId, @PathVariable(name = "articleId") int articleId){
        LikesDto.Toggle isLikedArticle = likesService.toggleArticleLikes(memberId, articleId);
        ResultResponse response;
        if(isLikedArticle.isLiked() == true){
            response = ResultResponse.of(ResponseCode.LIKES_CREATE_SUCCESS, isLikedArticle);
        } else {
            response = ResultResponse.of(ResponseCode.LIKES_DELETE_SUCCESS, isLikedArticle);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @PutMapping("/api/performs/member/{memberId}/likes/{performArticleId}")
    public ResponseEntity<ResultResponse> createPerformLikes(@PathVariable(name = "memberId") int memberId, @PathVariable(name = "performArticleId") int performArticleId){
        LikesDto.Toggle isLikedPerformArticle = likesService.togglePerformArticleLikes(memberId, performArticleId);
        ResultResponse response;
        if(isLikedPerformArticle.isLiked() == true){
            response = ResultResponse.of(ResponseCode.LIKES_CREATE_SUCCESS, isLikedPerformArticle);
        } else {
            response = ResultResponse.of(ResponseCode.LIKES_DELETE_SUCCESS, isLikedPerformArticle);
        }
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 일반 게시글 좋아요 등록 API
     */
    @PostMapping("/api/articles/likes/{articleId}")
    public ResponseEntity<ResultResponse> createLikes(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @PathVariable(name = "articleId") int articleId){

        String loginUserName = loginCheck(customUserDetails.getUsername());
        LikesDto.Response likesResponse = likesService.createLikes(loginUserName,articleId);

        ResultResponse response = ResultResponse.of(ResponseCode.LIKES_CREATE_SUCCESS, likesResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 일반 게시글 좋아요 삭제 API
     */
    @DeleteMapping("/api/articles/likes/{articleId}")
    public ResponseEntity<ResultResponse> deleteLikes(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @PathVariable(name = "articleId") int articleId){

        String loginUserName = loginCheck(customUserDetails.getUsername());
        LikesDto.Response likesResponse = likesService.deleteLikes(loginUserName,articleId);

        ResultResponse response = ResultResponse.of(ResponseCode.LIKES_DELETE_SUCCESS, likesResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 공연 게시글 좋아요 등록 API
     */
    @PostMapping("/api/performs/likes/{performArticleId}")
    public ResponseEntity<ResultResponse> createPerformLikes(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @PathVariable(name = "performArticleId") int performArticleId){

        String loginUserName = loginCheck(customUserDetails.getUsername());
        LikesDto.Response likesResponse = likesService.createPerformLikes(loginUserName,performArticleId);

        ResultResponse response = ResultResponse.of(ResponseCode.LIKES_CREATE_SUCCESS, likesResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 공연 게시글 좋아요 삭제 API
     */
    @DeleteMapping("/api/performs/likes/{performArticleId}")
    public ResponseEntity<ResultResponse> deletePerformLikes(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                      @PathVariable(name = "performArticleId") int performArticleId){

        String loginUserName = loginCheck(customUserDetails.getUsername());
        LikesDto.Response likesResponse = likesService.deletePerformLikes(loginUserName,performArticleId);

        ResultResponse response = ResultResponse.of(ResponseCode.LIKES_DELETE_SUCCESS, likesResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    private String loginCheck(String username){
        String loginUserName = memberRepository.findByUsername(username).getUsername();
        if(loginUserName.isEmpty() || loginUserName == null){
            throw new CustomException(ErrorCode.NEED_LOGIN);
        }
        return loginUserName;
    }
}
