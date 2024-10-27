package jsl.moum.chatappmodule.redis;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
public class ConnectedUser {
    private String userId;
    private Long roomNum;

    public ConnectedUser(String userId, Long roomNum) {
        this.userId = userId;
        this.roomNum = roomNum;
    }
}
