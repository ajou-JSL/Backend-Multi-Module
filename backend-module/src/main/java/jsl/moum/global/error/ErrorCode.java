package jsl.moum.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    // Common
    INTERNAL_SERVER_ERROR(500, "F-C001", "internal server error"),
    INVALID_INPUT_VALUE(400, "F-C002", "invalid input type"),
    METHOD_NOT_ALLOWED(405, "F-C003", "method not allowed"),
    INVALID_TYPE_VALUE(400, "F-C004", "invalid type value"),
    BAD_CREDENTIALS(400, "F-C005", "bad credentials"),
    ILLEGAL_ARGUMENT(400, "F-C006", "유효하지 않은 데이터입니다."),
    FILE_UPLOAD_FAIL(400, "F-C007", "Multipart Exception : File upload failed"),
    FILE_UPDATE_FAIL(400, "F-C008", "Multipart Exception : File update failed"),
    BASE64_PROCESS_FAIL(400, "F-C009", "base64 -> json 실패"),
    INVALID_SORT_BY_FIELD(400, "F-C010", "유효하지 않은 정렬(sortBy) 값입니다."),
    INVALID_FILE_TYPE(415, "F-C011", "유효하지 않은 파일 형식입니다."),


    // Jwt
    JWT_TOKEN_INVALID(400,"F-J001","유효하지 않은 JWT토큰"),

    // Member
    MEMBER_NOT_EXIST(404, "F-M001", "존재하지 않은 회원입니다."),
    USER_NAME_ALREADY_EXISTS(409, "F-M002", "이미 존재하는 아이디입니다."),
    NO_AUTHORITY(403, "F-M003", "권한이 없습니다."),
    NEED_LOGIN(401, "F-M004", "로그인을 해야합니다."),
    AUTHENTICATION_NOT_FOUND(401, "F-M005", "인증되지 않은 회원입니다."),
    MEMBER_ALREADY_LOGOUT(400, "F-M006", "이미 로그아웃 하였습니다. 먼저 로그인을 해주세요."),
    SIGN_OUT_MEMBER(400, "F-M007", "탈퇴한 회원입니다. 재가입이 필요합니다.#"),
    BANNED_MEMBER(401, "F-M008", "밴 당한 계정입니다."),

    // Auth
    LOGIN_FAIL(400, "F-A001", "아이디 또는 비밀번호가 유효하지 않습니다."),
    REFRESH_TOKEN_INVALID(400, "F-A002", "유효하지 않은 refresh 토큰 입니다."),
    JWT_TOKEN_EXPIRED(401, "F-A003", "jwt 토큰이 만료되었습니다."),

    EMAIL_VERIFY_FAILED(422,"F-E001","이메일 인증이 실패하였습니다."),
    EMAIL_ALREADY_VERIFIED(409, "F-E002", "이미 인증 완료한 이메일입니다."),

    // Article
    ARTICLE_NOT_FOUND(404,"F-AT001","게시글을 찾을 수 없습니다."),
    ARTICLE_DETAILS_NOT_FOUND(404,"F-AT001","게시글 상세 내용을 찾을 수 없습니다."),
    ARTICLE_ALREADY_DELETED(404,"F-AT002","이미 삭제된 게시글입니다."),

    // PerForm Article
    PERFORM_ARTICLE_NOT_FOUND(404,"F-PA001","공연 게시글을 찾을 수 없습니다."),
    PERFORM_ARTICLE_ALREADY_DELETED(404,"F-PA002","이미 삭제된 공연 게시글입니다."),
    PERFORM_ARTICLE_ALREADY_EXIST(403,"F-PA003","이미 게시된 공연 게시글이 존재합니다."),

    // Wishlist
    ALREADY_IN_WISHLIST(400,"F-W001","이미 위시리스트에 추가된 게시글입니다."),
    ALREADY_DELETED_WISHLIST(400,"F-W002","이미 위시리스트에서 삭제된 게시글입니다."),

    // Comment
    COMMENT_NOT_FOUND(404,"F-CM001","댓글을 찾을 수 없습니다."),
    COMMENT_ALREADY_DELETED(404,"F-CM002","이미 삭제된 댓글입니다."),

    // Likes
    DUPLICATE_LIKES(409, "F-L001","이미 좋아요 누른 게시글입니다."),
    LIKES_NOT_FOUND(404, "F-L002","존재하지 않는 좋아요입니다."),
    CANNOT_CREATE_SELF_LIKES(409, "F-L003","자신의 게시글에는 좋아요를 누를 수 없습니다."),
    CANNOT_DELETE_OTHERS_LIKES(403, "F-L004","본인이 등록한 좋아요만 취소할 수 있습니다."),

    // Chatroom
    CHATROOM_LIST_GET_FAIL(400, "F-CH001", "채팅방 목록 호출 실패"),
    CHATROOM_MEMBER_LIST_GET_FAIL(400, "F-CH002", "채팅방 멤버 목록 호출 실패"),
    CHATROOM_CREATE_FAIL(400, "F-CH003", "채팅방 생성 실패"),
    CHATROOM_UPDATE_FAIL(400, "F-CH004", "채팅방 수정 실패"),
    CHATROOM_MEMBER_INVITE_FAIL(400, "F-CH005", "채팅방에 멤버(들) 초대 실패"),
    CHATROOM_MEMBER_REMOVE_FAIL(400, "F-CH006", "채팅방에서 멤버(들) 삭제 실패"),
    CHATROOM_FIND_FAIL(404, "F-CH007", "채팅방이 존재하지 않습니다"),

    // Team
    MEMBER_ALREADY_INVITED(400, "F-T001", "이미 초대된 멤버입니다."),
    TEAM_NOT_FOUND(404, "F-T002", "팀을 찾을 수 없습니다."),
    NOT_TEAM_MEMBER(404, "F-T003", "팀에 속한 멤버가 아닙니다."),
    LEADER_CANNOT_LEAVE(401, "F-T004", "리더가 팀을 떠날 수 없습니다."),
    NEED_TEAM(401, "F-T005", "팀에 먼저 가입해야 합니다."),
    MAX_TEAM_LIMIT_EXCEEDED(400, "F-T006","팀 생성 최대 개수 초과하였습니다."),

    // Report
    REPORT_NOT_FOUND(404, "F-RP001", "신고 내역을 찾을 수 없습니다."),
    REPORT_MEMBER_FAIL(400, "F-RP002", "사용자 신고 오류"),
    REPORT_TEAM_FAIL(400, "F-RP003", "음악단체 신고 오류"),
    REPORT_ARTICLE_FAIL(400, "F-RP004", "게시글 신고 오류"),
    REPORT_MEMBER_EXISTS(400, "F-RP012", "해당 사용자를 이미 신고했습니다"),
    REPORT_TEAM_EXISTS(400, "F-RP013", "해당 음악단체를 이미 신고했습니다"),
    REPORT_ARTICLE_EXISTS(400, "F-RP014", "해당 게시글를 이미 신고했습니다"),

    // Business
    REQUIRED_FIELDS_MISSING(400, "F-B001", "필수 입력값이 누락되었습니다."),
    REGISTER_PRACTICE_ROOM_FAIL(400, "F-B002", "연습실 등록 오류"),
    REGISTER_PERFORMANCE_HALL_FAIL(400, "F-B003", "공연장 등록 오류"),
    PRACTICE_ROOM_NOT_FOUND(404, "F-B004", "해당 연습실을 찾을 수 없습니다."),
    PERFORMANCE_HALL_NOT_FOUND(404, "F-B005", "해당 공연장을 찾을 수 없습니다."),
    GET_PRACTICE_HALL_LIST_FAIL(400, "F-B006", "연습실 목록 조회 오류"),
    GET_PERFORMANCE_HALL_LIST_FAIL(400, "F-B007", "공연장 목록 조회 오류"),
    IMAGE_LIMIT_EXCEEDED(400, "F-B011", "이미지 업로드 개수 제한을 초과하였습니다."),
    INVALID_PAGE_VALUES(400, "F-B021", "페이지 값이 유효하지 않습니다."),

    // Naver Maps
    GET_LOCATION_INFO_FAIL(404,"F-NM001","위치 정보 조회 실패"),

    // Pamphlet
    PAMPHLET_NOT_FOUND(404,"F-PM001","팸플릿을 찾을 수 없습니다."),

    // QR Code
    QR_GENERATE_FAIL(400, "F-QR001", "QR 코드 생성 실패"),

    // Moum
    MAX_MOUM_LIMIT_EXCEEDED(400, "F-MM001","모음 생성 최대 개수 초과하였습니다."),
    ALREADY_FINISHED_MOUM(401,"F-MM002","이미 끝난 모음입니다."),
    NOT_FINISHED_MOUM(401,"F-MM003","이미 진행중인 모음입니다."),
    MOUM_NOT_FOUND(404,"F-MM003","존재하지 않는 모음입니다."),
    MOUM_PRACTICE_ROOM_NOT_FOUND(404,"F-MM004","모음에 등록된 연습실이 없습니다."),
    MOUM_PERFORMANCE_HALL_NOT_FOUND(404,"F-MM005","모음에 등록된 공연장이 없습니다."),;


    private final int status;
    private final String code;
    private final String message;
}