package jsl.moum.chatappmodule.chat;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class RedisConnectedUser {
    private String userId;
    private Long roomNum;

    public RedisConnectedUser(String userId, Long roomNum) {
        this.userId = userId;
        this.roomNum = roomNum;
    }
}
