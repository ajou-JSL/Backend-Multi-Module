package study.moum.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    // Member
    REGISTER_SUCCESS(200, "M001", "회원가입 되었습니다."),
    LOGIN_SUCCESS(200, "M002", "로그인 되었습니다."),
    REISSUE_SUCCESS(200, "M003", "재발급 되었습니다."),
    LOGOUT_SUCCESS(200, "M004", "로그아웃 되었습니다."),
    GET_MY_INFO_SUCCESS(200, "M005", "내 정보 조회 완료"),

    // TEAM
    CREATE_TEAM_SUCCESS(201, "T001", "팀 생성 성공."),
    UPDATE_TEAM_SUCCESS(201, "T002", "팀 정보 수정 성공."),
    DELETE_TEAM_SUCCESS(200, "T003", "팀 삭제 성공"),
    GET_TEAM_SUCCESS(200, "T005", "팀 단건 조회 성공."),
    GET_TEAM_LIST_SUCCESS(200, "T006", "전체 팀 리스트 조회 성공"),
    GET_MY_TEAM_LIST_SUCCESS(200, "T007", "내 팀 리스트 조회 성공"),
    INVITE_MEMBER_SUCCESS(201, "T008", "멤버를 팀에 초대했습니다."),
    KICK_MEMBER_SUCCESS(200, "T009", "멤버를 팀에서 강퇴하였습니다."),
    LEAVE_TEAM_SUCCESS(200,"T010","팀에서 탈퇴했습니다."),

    // Article
    ARTICLE_LIST_GET_SUCCESS(200,"A001","게시글 목록 조회 성공."),
    ARTICLE_GET_SUCCESS(200,"A002","게시글 단건 조회 성공."),
    ARTICLE_POST_SUCCESS(201,"A003","게시글 등록 성공."),
    ARTICLE_UPDATE_SUCCESS(201,"A003","게시글 수정 성공."),
    ARTICLE_DELETE_SUCCESS(200,"A003","게시글 삭제 성공."),

    // Jwt
    ACCESS_TOKEN(200, "J001", "액세스 토큰 발급 성공"),

    // Email
    EMAIL_SEND_SUCCESS(200,"E001","인증 이메일 발송 성공하였습니다."),
    EMAIL_VERIFY_SUCCESS(200,"E001","이메일 인증 성공하였습니다."),

    // Commenet
    COMMENT_CREATE_SUCCESS(201,"C001","댓글 작성 성공"),
    COMMENT_UPDATE_SUCCESS(201,"C002","댓글 수정 성공"),
    COMMENT_DELETE_SUCCESS(200,"C003","댓글 삭제 성공"),

    // Likes
    LIKES_CREATE_SUCCESS(201,"L001","좋아요 등록 성공"),
    LIKES_DELETE_SUCCESS(200,"L002","좋아요 삭제 성공"),

    // Records
    RECORD_ADD_SUCCESS(201,"R001","이력 등록 성공"),
    RECORD_DELETE_SUCCESS(200,"R002","이력 삭제 성공"),
    RECORD_GET_SUCCESS(200,"R003","이력 조회 성공"),
    RECORD_LIST_GET_SUCCESS(200,"R004","이력 목록 조회 성공"),

    // Profiles
    GET_PROFILE_SUCCESS(200,"P001","프로필 조회 성공"),
    UPDATE_PROFILE_SUCCESS(201,"P002","프로필 업데이트 성공");


    private final int status;
    private final String code;
    private final String message;
}