package jsl.moum.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    // Member
    REGISTER_SUCCESS(200, "S-M001", "회원가입 되었습니다."),
    LOGIN_SUCCESS(200, "S-M002", "로그인 되었습니다."),
    REISSUE_SUCCESS(200, "S-M003", "재발급 되었습니다."),
    LOGOUT_SUCCESS(200, "S-M004", "로그아웃 되었습니다."),
    GET_MY_INFO_SUCCESS(200, "S-M005", "내 정보 조회 완료"),

    // TEAM
    CREATE_TEAM_SUCCESS(201, "S-T001", "팀 생성 성공."),
    UPDATE_TEAM_SUCCESS(201, "S-T002", "팀 정보 수정 성공."),
    DELETE_TEAM_SUCCESS(200, "S-T003", "팀 삭제 성공"),
    GET_TEAM_SUCCESS(200, "S-T005", "팀 단건 조회 성공."),
    GET_TEAM_LIST_SUCCESS(200, "S-T006", "전체 팀 리스트 조회 성공"),
    GET_MY_TEAM_LIST_SUCCESS(200, "S-T007", "내 팀 리스트 조회 성공"),
    INVITE_MEMBER_SUCCESS(201, "S-T008", "멤버를 팀에 초대했습니다."),
    KICK_MEMBER_SUCCESS(200, "S-T009", "멤버를 팀에서 강퇴하였습니다."),
    LEAVE_TEAM_SUCCESS(200,"S-T010","팀에서 탈퇴했습니다."),

    // Article
    ARTICLE_LIST_GET_SUCCESS(200,"S-A001","게시글 목록 조회 성공."),
    ARTICLE_GET_SUCCESS(200,"S-A002","게시글 단건 조회 성공."),
    ARTICLE_POST_SUCCESS(201,"S-A003","게시글 등록 성공."),
    ARTICLE_UPDATE_SUCCESS(201,"S-A004","게시글 수정 성공."),
    ARTICLE_DELETE_SUCCESS(200,"S-A005","게시글 삭제 성공."),

    // Jwt
    ACCESS_TOKEN(200, "S-J001", "액세스 토큰 발급 성공"),

    // Email
    EMAIL_SEND_SUCCESS(200,"S-E001","인증 이메일 발송 성공하였습니다."),
    EMAIL_VERIFY_SUCCESS(200,"S-E002","이메일 인증 성공하였습니다."),

    // Commenet
    COMMENT_CREATE_SUCCESS(201,"S-C001","댓글 작성 성공"),
    COMMENT_UPDATE_SUCCESS(201,"S-C002","댓글 수정 성공"),
    COMMENT_DELETE_SUCCESS(200,"S-C003","댓글 삭제 성공"),

    // Likes
    LIKES_CREATE_SUCCESS(201,"S-L001","좋아요 등록 성공"),
    LIKES_DELETE_SUCCESS(200,"S-L002","좋아요 삭제 성공"),

    // Records
    RECORD_ADD_SUCCESS(201,"S-R001","이력 등록 성공"),
    RECORD_DELETE_SUCCESS(200,"S-R002","이력 삭제 성공"),
    RECORD_GET_SUCCESS(200,"S-R003","이력 조회 성공"),
    RECORD_LIST_GET_SUCCESS(200,"S-R004","이력 목록 조회 성공"),

    // Profiles
    GET_PROFILE_SUCCESS(200,"S-P001","프로필 조회 성공"),
    UPDATE_PROFILE_SUCCESS(201,"S-P002","프로필 업데이트 성공"),

    // Chatroom
    CHATROOM_LIST_GET_SUCCESS(200, "S-CH001", "채팅방 목록 호출 성공"),
    CHATROOM_MEMBER_LIST_GET_SUCCESS(200, "S-CH002", "채팅방 멤버 목록 호출 성공"),
    CHATROOM_CREATE_SUCCESS(200, "S-CH003", "채팅방 생성 성공"),
    CHATROOM_UPDATE_SUCCESS(201, "S-CH004", "채팅방 수정 성공"),
    CHATROOM_INVITE_SUCCESS(200, "S-CH005", "채팅방에 멤버(들) 초대 성공"),
    CHATROOM_MEMBER_REMOVE_SUCCESS(200, "S-CH006", "채팅방에서 멤버(들) 삭제 성공"),
    CHATROOM_FIND_SUCCESS(200, "S-CH007", "채팅방 조회 성공"),

    // Moum
    CREATE_MOUM_SUCCESS(201,"S-MM001", "모음 생성 성공"),
    GET_MOUM_SUCCESS(200,"S-MM002", "모음 조회 성공"),
    UPDATE_MOUM_SUCCESS(201,"S-MM003", "모음 수정 성공"),
    DELETE_MOUM_SUCCESS(200,"S-MM004", "모음 삭제 성공"),
    FINISH_MOUM_SUCCESS(201,"S-MM005", "모음 마감하기 성공"),
    REOPEN_MOUM_SUCCESS(201,"S-MM006", "모음 되살리기 성공"),
    UPDATE_MOUM_PROCESS_SUCCESS(201,"S-MM007", "모음 진행률 상태 수정 성공"),

    // Perform
    CREATE_PERFORM_ARTICLE_SUCCESS(201,"S-P001","공연게시글 생성 성공"),
    GET_PERFORM_ARTICLE_SUCCESS(200,"S-P002","공연게시글 단건 조회 성공"),
    GET_ALL_PERFORM_ARTICLE_SUCCESS(200,"S-P003","공연게시글 리스트 조회 성공");


    private final int status;
    private final String code;
    private final String message;
}