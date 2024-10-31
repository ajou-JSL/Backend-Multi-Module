package study.moum.community.article.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import study.moum.auth.domain.CustomUserDetails;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.community.article.domain.article.ArticleEntity;

import study.moum.community.article.dto.ArticleDetailsDto;
import study.moum.community.article.dto.ArticleDto;
import study.moum.community.article.service.ArticleService;
import study.moum.global.error.exception.NeedLoginException;
import study.moum.global.response.ResponseCode;
import study.moum.global.response.ResultResponse;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ArticleController {

    private final ArticleService articleService;

    /**
     * 게시글 목록 조회 API
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
     */
    @GetMapping("/api/articles/{id}")
    public ResponseEntity<ResultResponse> getArticleById(@PathVariable int id){
        ArticleDetailsDto.Response  articleDetailsResponse = articleService.getArticleById(id);
        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_GET_SUCCESS, articleDetailsResponse);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 게시글 작성 API
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
