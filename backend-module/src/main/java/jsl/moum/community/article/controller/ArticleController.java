package jsl.moum.community.article.controller;

import jakarta.validation.Valid;
import jsl.moum.auth.domain.CustomUserDetails;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.community.article.dto.ArticleDetailsDto;
import jsl.moum.community.article.dto.ArticleDto;
import jsl.moum.community.article.service.ArticleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.community.article.domain.article.ArticleEntity;

import jsl.moum.global.error.exception.NeedLoginException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
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
    public ResponseEntity<ResultResponse> getArticleById(@PathVariable int id, @AuthenticationPrincipal CustomUserDetails customUserDetails){
        ArticleDetailsDto.Response articleDetailsResponse = articleService.getArticleById(id, customUserDetails.getUsername());
        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_GET_SUCCESS, articleDetailsResponse);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 게시글 작성 API
     */
    @PostMapping("/api/articles")
    public ResponseEntity<ResultResponse> postArticle(
            @Valid @RequestPart(value = "articleRequestDto") ArticleDto.Request articleRequestDto,
            @RequestPart(value = "file", required = false) List<MultipartFile> files, // MultipartFile 추가
            @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException {

        if (customUserDetails.getUsername() == null) {
            throw new NeedLoginException();
        }

        ArticleDto.Response articleResponse = articleService.postArticle(articleRequestDto, files, customUserDetails.getUsername());

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
            @RequestPart(value = "file", required = false) List<MultipartFile> files, // MultipartFile 추가
            @AuthenticationPrincipal CustomUserDetails customUserDetails) throws IOException {

        ArticleDetailsDto.Response articleResponse = articleService.updateArticleDetails(id, articleDetailsRequestDto, files, customUserDetails.getUsername());
        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_UPDATE_SUCCESS, articleResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 게시글 삭제 API
     */
    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<ResultResponse> deleteArticle(
            @PathVariable(name = "id") int id,
            @AuthenticationPrincipal CustomUserDetails customUserDetails){

        ArticleDto.Response articleResponse = articleService.deleteArticleDetails(id, customUserDetails.getUsername());

        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_DELETE_SUCCESS, articleResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 게시글 검색 API
     */
    @GetMapping("/api/articles-all/search")
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
     * 카테고리별 게시글 목록 조회
     */
    @GetMapping("/api/articles-all/category")
    public ResponseEntity<ResultResponse> getArticlesWithCategory(
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
        if(customUserDetails.getUsername() == null){
            throw new NeedLoginException();
        }

        List<ArticleDto.Response> articleList = articleService.getMyWishlist(customUserDetails.getUsername(), page,size);

        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_LIST_GET_SUCCESS, articleList);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));

    }

    /**
     * 실시간 인기게시글 조회 API
     */
    @GetMapping("/api/articles/hot")
    public ResponseEntity<ResultResponse> getHotArticleList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
                                                        @RequestParam(defaultValue = "0") int page,
                                                        @RequestParam(defaultValue = "10") int size){
        if(customUserDetails.getUsername() == null){
            throw new NeedLoginException();
        }

        List<ArticleDto.Response> articleList = articleService.getHotArticleList(page,size);

        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_LIST_GET_SUCCESS, articleList);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));

    }

    /**
     * 필터링으로 게시글 목록 조회
     */
    @GetMapping("/api/articles/search")
    public ResponseEntity<ResultResponse> getArticlesByFiltering(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Boolean filterByLikesCount,
            @RequestParam(required = false) Boolean filterByViewCount,
            @RequestParam(required = false) Boolean filterByCommentsCount,
            @RequestParam(required = false) Boolean filterByCreatedAt,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime createdAt,
            @RequestParam(required = false) ArticleEntity.ArticleCategories category,
            @RequestParam(required = false) MusicGenre genre,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        ArticleDto.SearchDto searchDto = ArticleDto.SearchDto.builder()
                .keyword(keyword)
                .genre(genre)
                .category(category)
                .createdAt(createdAt)
                .filterByCommentsCount(filterByCommentsCount)
                .filterByCreatedAt(filterByCreatedAt)
                .filterByLikesCount(filterByLikesCount)
                .filterByViewCount(filterByViewCount)
                .build();

        log.info("SearchDto details: keyword={}, genre={}, category={}, createdAt={}, getFilterByCommentsCount={}," +
                        "getFilterByViewCount={}, getFilterByLikesCount={}, getFilterByCreatedAt={}",
                searchDto.getKeyword(),
                searchDto.getGenre(),
                searchDto.getCategory(),
                searchDto.getCreatedAt(),
                searchDto.getFilterByCommentsCount(),
                searchDto.getFilterByViewCount(),
                searchDto.getFilterByLikesCount(),
                searchDto.getFilterByCreatedAt()
        );

        Page<ArticleDto.Response> articleList = articleService.getArticlesByFiltering(searchDto,page,size);

        ResultResponse response = ResultResponse.of(ResponseCode.ARTICLE_LIST_GET_SUCCESS, articleList);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

}
