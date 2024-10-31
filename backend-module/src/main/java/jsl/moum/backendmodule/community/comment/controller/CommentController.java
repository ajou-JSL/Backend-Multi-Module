package jsl.moum.backendmodule.community.comment.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jsl.moum.backendmodule.auth.domain.CustomUserDetails;
import jsl.moum.backendmodule.community.comment.dto.CommentDto;
import jsl.moum.backendmodule.community.comment.service.CommentService;
import jsl.moum.backendmodule.global.response.ResponseCode;
import jsl.moum.backendmodule.global.response.ResultResponse;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 생성 API
     */
    @PostMapping("/api/comments/{articleId}")
    public ResponseEntity<ResultResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable int articleId,
            @Valid @RequestBody CommentDto.Request commentRequestDto)
    {
        CommentDto.Response commentResponse = commentService.createComment(commentRequestDto, customUserDetails.getUsername(), articleId);
        ResultResponse response = ResultResponse.of(ResponseCode.COMMENT_CREATE_SUCCESS, commentResponse);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 댓글 수정 API 엔드포인트.
     */
    @PatchMapping("/api/comments/{commentId}")
    public ResponseEntity<ResultResponse> updateComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable int commentId,
            @Valid @RequestBody CommentDto.Request commentRequestDto
    ){

        CommentDto.Response commentResponse = commentService.updateComment(commentRequestDto, customUserDetails.getUsername(), commentId);

        ResultResponse response = ResultResponse.of(ResponseCode.COMMENT_UPDATE_SUCCESS, commentResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 댓글 삭제 API 엔드포인트.
     */
    @DeleteMapping("/api/comments/{commentId}")
    public ResponseEntity<ResultResponse> deleteComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable int commentId
    ){
        CommentDto.Response commentResponse = commentService.deleteComment(customUserDetails.getUsername(), commentId);

        ResultResponse response = ResultResponse.of(ResponseCode.COMMENT_DELETE_SUCCESS, commentResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
}