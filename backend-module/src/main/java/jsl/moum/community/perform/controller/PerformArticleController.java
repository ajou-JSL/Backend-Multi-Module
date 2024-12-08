package jsl.moum.community.perform.controller;

import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.community.perform.dto.PerformArticleDto;
import jsl.moum.community.perform.dto.PerformArticleUpdateDto;
import jsl.moum.community.perform.service.PerformArticleService;
import jsl.moum.global.error.exception.NeedLoginException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.moum.team.dto.TeamDto;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
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
        수정
    */
    @PatchMapping("/api/performs/{performArticleId}")
    public ResponseEntity<ResultResponse> updatePerformArticle(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                               @PathVariable int performArticleId,
                                                               @RequestPart(value = "requestDto") PerformArticleUpdateDto.Request requestDto,
                                                               @RequestPart(value = "file", required = false)MultipartFile file) throws IOException {
        String username = loginCheck(customUserDetails.getUsername());
        PerformArticleUpdateDto.Response responseDto = performArticleService.updatePerformArticle(username, performArticleId, requestDto, file);
        ResultResponse result = ResultResponse.of(ResponseCode.UPDATE_PERFORM_ARTICLE_SUCCESS,responseDto);
        return new ResponseEntity<>(result, HttpStatusCode.valueOf(result.getStatus()));
    }

    /*
        삭제
    */
    @DeleteMapping("/api/performs/{performArticleId}")
    public ResponseEntity<ResultResponse> deletePerformArticle(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                               @PathVariable int performArticleId){
        String username = loginCheck(customUserDetails.getUsername());
        PerformArticleDto.Response responseDto = performArticleService.deletePerformArticle(username, performArticleId);
        ResultResponse result = ResultResponse.of(ResponseCode.DELETE_PERFORM_ARTICLE_SUCCESS,responseDto);
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
        Page<PerformArticleDto.Response> responseDto = performArticleService.getAllThisMonthPerformArticles(page, size);
        ResultResponse result = ResultResponse.of(ResponseCode.GET_THIS_MONTH_PERFORM_ARTICLE_SUCCESS,responseDto);
        return new ResponseEntity<>(result, HttpStatusCode.valueOf(result.getStatus()));
    }

    /*
        모음의 공연 게시글 조회
    */
    @GetMapping("/api/performs")
    public ResponseEntity<ResultResponse> getPerformArticleByMoumId(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                         @RequestParam(value = "moumId", required = true) int moumId){
        loginCheck(customUserDetails.getUsername());
        PerformArticleDto.Response responseDto = performArticleService.getPerformArticleByMoumId(moumId);
        ResultResponse result = ResultResponse.of(ResponseCode.GET_PERFORM_ARTICLE_SUCCESS,responseDto);
        return new ResponseEntity<>(result, HttpStatusCode.valueOf(result.getStatus()));
    }

    /**
     * 필터링으로 팀 리스트 조회
     */
    @GetMapping("/api/performs-all/search")
    public ResponseEntity<ResultResponse> getPerformArticlesWithFiltering(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                                          @RequestParam(required = false) Boolean filterByCreatedAt,
                                                                          @RequestParam(required = false) Boolean filterByLikesCount,
                                                                          @RequestParam(required = false) Boolean filterByViewCount,
                                                                          @RequestParam(required = false) String keyword,
                                                                          @RequestParam(required = false) MusicGenre genre,
                                                                          @RequestParam(required = false) String location,
                                                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date startDate,
                                                                          @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date endDate,
                                                                          @RequestParam(defaultValue = "0") int page,
                                                                          @RequestParam(defaultValue = "10") int size) {

        {
            PerformArticleDto.SearchDto searchDto = PerformArticleDto.SearchDto.builder()
                    .genre(genre)
                    .location(location)
                    .keyword(keyword)
                    .startDate(startDate)
                    .endDate(endDate)
                    .filterByCreatedAt(filterByCreatedAt)
                    .filterByLikesCount(filterByLikesCount)
                    .filterByViewCount(filterByViewCount)
                    .build();
            loginCheck(customUserDetails.getUsername());
            Page<PerformArticleDto.Response> responseDto = performArticleService.getPerformArticleWithFiltering(searchDto, page, size);
            ResultResponse response = ResultResponse.of(ResponseCode.GET_PERFORM_ARTICLE_SUCCESS, responseDto);
            return new ResponseEntity<>(response, HttpStatusCode.valueOf(response.getStatus()));
        }


    }
    public String loginCheck(String username){
        if(username == null){
            throw new NeedLoginException();
        }

        return username;
    }
}
