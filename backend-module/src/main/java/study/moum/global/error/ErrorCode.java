package study.moum.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(500, "C001", "internal server error"),
    INVALID_INPUT_VALUE(400, "C002", "invalid input type"),
    METHOD_NOT_ALLOWED(405, "C003", "method not allowed"),
    INVALID_TYPE_VALUE(400, "C004", "invalid type value"),
    BAD_CREDENTIALS(400, "C005", "bad credentials"),
    ILLEGAL_ARGUMENT(400, "C006", "유효하지 않은 데이터입니다."),

    // Member
    MEMBER_NOT_EXIST(404, "M001", "존재하지 않은 회원입니다."),
    USER_NAME_ALREADY_EXISTS(409, "M002", "이미 존재하는 아이디입니다."),
    NO_AUTHORITY(403, "M003", "권한이 없습니다."),
    NEED_LOGIN(401, "M004", "로그인을 해야합니다."),
    AUTHENTICATION_NOT_FOUND(401, "M005", "인증되지 않은 회원입니다."),
    MEMBER_ALREADY_LOGOUT(400, "M006", "이미 로그아웃 하였습니다. 먼저 로그인을 해주세요."),

    // Auth
    LOGIN_FAIL(400, "A001", "아이디 또는 비밀번호가 유효하지 않습니다."),
    REFRESH_TOKEN_INVALID(400, "A002", "유효하지 않은 refresh 토큰 입니다."),
    JWT_TOKEN_EXPIRED(401, "A003", "jwt 토큰이 만료되었습니다."),

    EMAIL_VERIFY_FAILED(422,"E001","이메일 인증이 실패하였습니다."),
    EMAIL_ALREADY_VERIFIED(409, "E001", "이미 인증 완료한 이메일입니다."),

    // Article
    ARTICLE_NOT_FOUND(404,"AT001","게시글을 찾을 수 없습니다."),
    ARTICLE_ALREADY_DELETED(404,"AT002","이미 삭제된 게시글입니다."),

    // Wishlist
    ALREADY_IN_WISHLIST(400,"W001","이미 위시리스트에 추가된 게시글입니다."),
    ALREADY_DELETED_WISHLIST(400,"W002","이미 위시리스트에서 삭제된 게시글입니다."),

    // Comment
    COMMENT_NOT_FOUND(404,"CM001","댓글을 찾을 수 없습니다."),
    COMMENT_ALREADY_DELETED(404,"CM002","이미 삭제된 댓글입니다."),

    // Likes
    DUPLICATE_LIKES(409, "L001","이미 좋아요 누른 게시글입니다."),
    LIKES_NOT_FOUND(404, "L002","찾을 수 없는 좋아요입니다."),
    CANNOT_CREATE_SELF_LIKES(409, "L003","자신의 게시글에는 좋아요를 누를 수 없습니다."),
    CANNOT_DELETE_OTHERS_LIKES(409, "L004","본인이 등록한 좋아요만 취소할 수 있습니다."),

    // Team
    MEMBER_ALREADY_INVITED(400, "T001", "이미 초대된 멤버입니다."),
    TEAM_NOT_FOUND(404, "T002", "팀을 찾을 수 없습니다."),
    NOT_TEAM_MEMBER(404, "T003", "팀에 속한 멤버가 아닙니다."),
    LEADER_CANNOT_LEAVE(401, "T004", "리더가 팀을 떠날 수 없습니다.");


    private final int status;
    private final String code;
    private final String message;
}