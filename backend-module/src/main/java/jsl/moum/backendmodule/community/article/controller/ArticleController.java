package jsl.moum.backendmodule.community.article.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.backendmodule.auth.domain.CustomUserDetails;
import jsl.moum.backendmodule.community.article.domain.article.ArticleEntity;
import jsl.moum.backendmodule.community.article.dto.ArticleDetailsDto;
import jsl.moum.backendmodule.community.article.dto.ArticleDto;
import jsl.moum.backendmodule.community.article.service.ArticleService;
import jsl.moum.backendmodule.global.error.exception.NeedLoginException;
import jsl.moum.backendmodule.global.response.ResponseCode;
import jsl.moum.backendmodule.global.response.ResultResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 게시글 목록 조회 API
     *
     * @param page
     * @param size
     * @return 게시글 목록
     */
    @GetMapping("/api/articles")
    public ResponseEntity<ResultResponse> getArticleList(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size) {
        List<ArticleDto.Response> articleList = articleService.getArticleList(page,size);

        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_LIST_GET_SUCCESS, articleList);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 게시글 상세 조회 API
     *
     * @param id 게시글의 ID
     * @return 게시글 단건
     */
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ResultResponse> getArticleById(@PathVariable int id){
        ArticleDetailsDto.Response  articleDetailsResponse = articleService.getArticleById(id);
        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_GET_SUCCESS, articleDetailsResponse);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 게시글 작성 API
     *
     * @param article 작성 요청 dto
     * @param file 멀티파트 파일
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @return 작성한 게시글
     */
    @PostMapping("/api/articles")
    public ResponseEntity<ResultResponse> postArticle(
            @Valid @RequestPart(value = "articleRequestDto") ArticleDto.Request articleRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file, // MultipartFile 추가
            @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException {

        if (customUserDetails == null) {
            throw new NeedLoginException();
        }

        ArticleDto.Response articleResponse = articleService.postArticle(articleRequestDto, file, customUserDetails.getUsername());

        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_POST_SUCCESS, articleResponse);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    /**
     * 게시글 수정 API
     *
     * @param id 게시글의 ID
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @param article 수정 요청 dto
     * @return 수정한 게시글
     */
    @PatchMapping("/api/articles/{id}")
    public ResponseEntity<ResultResponse> updateArticle(
            @PathVariable int id,
            @Valid @RequestPart ArticleDetailsDto.Request articleDetailsRequestDto,
            @RequestPart(value = "file", required = false) MultipartFile file, // MultipartFile 추가
            @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException {

        ArticleDetailsDto.Response articleResponse = articleService.updateArticleDetails(id, articleDetailsRequestDto, file, customUserDetails.getUsername());
        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_UPDATE_SUCCESS, articleResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 게시글 삭제 API
     *
     * @param id 게시글의 ID
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @return 삭제한 게시글
     */
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<ResultResponse> deleteArticle(
            @PathVariable int id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){

        ArticleDto.Response articleResponse = articleService.deleteArticleDetails(id, customUserDetails.getUsername());

        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_DELETE_SUCCESS, articleResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }



    /**
     * 게시글 검색 API
     *
     * @param keyword
     * @param category
     * @param page
     * @param size
     * @return 검색된 게시글 리스트
     */
    @GetMapping("/api/articles/search")
    public ResponseEntity<ResultResponse> searchArticles(@RequestParam(required = false) String keyword,
                                                         @RequestParam(required = false) String category,
                                                         @RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "10") int size
    ) {

        List<ArticleDto.Response> articleList = articleService.getArticleWithTitleSearch(keyword,category,page,size);

        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_LIST_GET_SUCCESS, articleList);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 게시글 목록 카테고리 필터링 조회 API
     *
     * @param category 게시글 카테고리 (FREE_TALKING_BOARD 또는 RECRUIT_BOARD)
     * @param category
     * @param page
     * @param size
     * @return 게시글 리스트
     */
    @GetMapping("/api/articles/category")
    public ResponseEntity<ResultResponse> getArticles(
            @RequestParam(required = true) ArticleEntity.ArticleCategories category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        List<ArticleDto.Response> articleList = articleService.getArticlesByCategory(category,page,size);

        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_LIST_GET_SUCCESS, articleList);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }


    /**
     * 위시리스트 목록 조회 API
     *
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @param page 페이지
     * @param size 사이즈
     * @return 위시리스트에 있는 게시글 리스트
     */
    @GetMapping("/api/articles/wishlist")
    public ResponseEntity<ResultResponse> getMyWishlist(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size){
        if(customUserDetails == null){
            throw new NeedLoginException();
        }

        List<ArticleDto.Response> articleList = articleService.getMyWishlist(customUserDetails.getUsername(), page,size);

        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_LIST_GET_SUCCESS, articleList);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));

    }

}
