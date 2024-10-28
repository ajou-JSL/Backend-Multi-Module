package jsl.moum.chatappmodule.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {

    // Chat
    CHAT_SEND_SUCCESS(200, "CH001", "채팅 메시지 전송 성공"),

    // Chat Error
    CHAT_SEND_FAILED(400, "CH001", "채팅 메시지 전송 실패"),

    // Jwt
    ACCESS_TOKEN(200, "J001", "액세스 토큰 발급 성공"),

    // Test
    TEST_SUCCESS(200, "T001", "테스트 성공");


    private final int status;
    private final String code;
    private final String message;
}