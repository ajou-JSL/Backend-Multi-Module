package jsl.moum.moum.lifecycle.dto;

import jsl.moum.moum.lifecycle.domain.entity.LifecyclePracticeRoom;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LifecyclePracticeRoomDto {
    private int id;
    private int moumId;
    private Integer roomId;
    private String roomName;

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private int moumId;
        private Integer roomId;
        private String roomName;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private int id;
        private int moumId;
        private Integer roomId;
        private String roomName;

        public Response(LifecyclePracticeRoom lifecyclePracticeRoom) {
            this.id = lifecyclePracticeRoom.getId();
            this.moumId = lifecyclePracticeRoom.getMoum().getId();
            this.roomId = lifecyclePracticeRoom.getRoomId();
            this.roomName = lifecyclePracticeRoom.getPracticeRoom();
        }
    }

}
