package jsl.moum.chatroom.dto;

import jsl.moum.chatroom.domain.Chatroom;
import lombok.*;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatroomDto {

    private String chatroomName;
    private int chatroomId;

    public ChatroomDto(Chatroom chatroom) {
        this.chatroomName = chatroom.getChatroomName();
        this.chatroomId = chatroom.getChatroomId();
    }


}
