package jsl.moum.community.perform.controller;

import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.community.perform.dto.PerformArticleDto;
import jsl.moum.community.perform.service.PerformArticleService;
import jsl.moum.global.error.exception.NeedLoginException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PerformArticleController {

    private final PerformArticleService performArticleService;

    /*
        생성
     */
    @PostMapping("/api/performs")
    public ResponseEntity<ResultResponse> createPerformArticle(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                               @RequestPart(value = "requestDto") PerformArticleDto.Request requestDto,
                                                               @RequestPart(value = "file", required = false)MultipartFile file) throws IOException {
        String username = loginCheck(customUserDetails.getUsername());
        PerformArticleDto.Response responseDto = performArticleService.createPerformArticle(username, requestDto, file);
        ResultResponse result = ResultResponse.of(ResponseCode.CREATE_PERFORM_ARTICLE_SUCCESS,responseDto);
        return new ResponseEntity<>(result, HttpStatusCode.valueOf(result.getStatus()));
    }

    /*
        단건 조회
     */
    @GetMapping("/api/performs/{performArticleId}")
    public ResponseEntity<ResultResponse> getPerformArticleById(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                @PathVariable int performArticleId){
        loginCheck(customUserDetails.getUsername());
        PerformArticleDto.Response responseDto = performArticleService.getPerformArticleById(performArticleId);
        ResultResponse result = ResultResponse.of(ResponseCode.GET_PERFORM_ARTICLE_SUCCESS,responseDto);
        return new ResponseEntity<>(result, HttpStatusCode.valueOf(result.getStatus()));
    }

    /*
        리스트 조회
    */
    @GetMapping("/api/performs-all")
    public ResponseEntity<ResultResponse> getAllPerformArticle(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        loginCheck(customUserDetails.getUsername());
        List<PerformArticleDto.Response> responseDto = performArticleService.getAllPerformArticle(page, size);
        ResultResponse result = ResultResponse.of(ResponseCode.GET_ALL_PERFORM_ARTICLE_SUCCESS,responseDto);
        return new ResponseEntity<>(result, HttpStatusCode.valueOf(result.getStatus()));
    }

    /*
        이달의 공연 게시글 리스트 조회
    */
    @GetMapping("/api/performs-all/this-month")
    public ResponseEntity<ResultResponse> getAllThisMonthPerformArticles(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                               @RequestParam(defaultValue = "0") int page,
                                                               @RequestParam(defaultValue = "10") int size){
        loginCheck(customUserDetails.getUsername());
        List<PerformArticleDto.Response> responseDto = performArticleService.getAllThisMonthPerformArticles(page, size);
        ResultResponse result = ResultResponse.of(ResponseCode.GET_THIS_MONTH_PERFORM_ARTICLE_SUCCESS,responseDto);
        return new ResponseEntity<>(result, HttpStatusCode.valueOf(result.getStatus()));
    }




    public String loginCheck(String username){
        if(username == null){
            throw new NeedLoginException();
        }

        return username;
    }
}
