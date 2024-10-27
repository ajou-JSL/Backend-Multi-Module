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
     *
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @param id 댓글을 달 게시글의 ID (경로 변수)
     * @param commentRequestDto 요청으로 들어온 댓글의 내용이 담긴 DTO (유효성 검증 적용)
     * @return 댓글 생성 성공 메시지와 생성된 댓글 정보를 담은 응답 객체
     */
    @PostMapping("/api/comments/{id}")
    public ResponseEntity<ResultResponse> createComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable int id,
            @Valid @RequestBody CommentDto.Request commentRequestDto)
    {
        CommentDto.Response commentResponse = commentService.createComment(commentRequestDto, customUserDetails.getUsername(), id);
        ResultResponse response = ResultResponse.of(ResponseCode.COMMENT_CREATE_SUCCESS, commentResponse);

        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 댓글 수정 API 엔드포인트.
     *
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @param id 수정할 댓글의 ID (경로 변수)
     * @param commentRequestDto 요청으로 들어온 수정할 내용이 담긴 DTO (유효성 검증 적용)
     * @return 댓글 수정 성공 메시지와 수정된 댓글 정보를 담은 응답 객체
     */
    @PatchMapping("/api/comments/{id}")
    public ResponseEntity<ResultResponse> updateComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable int id,
            @Valid @RequestBody CommentDto.Request commentRequestDto
    ){

        CommentDto.Response commentResponse = commentService.updateComment(commentRequestDto, customUserDetails.getUsername(), id);

        ResultResponse response = ResultResponse.of(ResponseCode.COMMENT_UPDATE_SUCCESS, commentResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    /**
     * 댓글 삭제 API 엔드포인트.
     *
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @param id 삭제할 댓글의 ID (경로 변수)
     * @return 댓글 삭제 성공 메시지와 삭제된 댓글 정보를 담은 응답 객체
     */
    @DeleteMapping("/api/comments/{id}")
    public ResponseEntity<ResultResponse> deleteComment(
            @AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable int id
    ){
        CommentDto.Response commentResponse = commentService.deleteComment(customUserDetails.getUsername(), id);

        ResultResponse response = ResultResponse.of(ResponseCode.COMMENT_DELETE_SUCCESS, commentResponse);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }
}