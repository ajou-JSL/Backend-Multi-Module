package jsl.moum.moum.lifecycle.dto;

import jsl.moum.moum.lifecycle.domain.LifecyclePracticeRoom;
import lombok.*;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LifecyclePracticeRoomDto {
    private int id;
    private int moumId;
    private String practiceRoom;

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Request {
        private int moumId;
        private String practiceRoom;
    }

    @Builder
    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private int id;
        private int moumId;
        private String practiceRoom;

        public Response(LifecyclePracticeRoom lifecyclePracticeRoom) {
            this.id = lifecyclePracticeRoom.getId();
            this.moumId = lifecyclePracticeRoom.getMoum().getId();
            this.practiceRoom = lifecyclePracticeRoom.getPracticeRoom();
        }
    }

}
