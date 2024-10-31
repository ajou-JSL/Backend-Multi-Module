package jsl.moum.backendmodule.chatroom.dto;

import jsl.moum.backendmodule.chatroom.domain.Chatroom;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ChatroomDto {

    private String chatroomName;
    private int chatroomId;

    public ChatroomDto(Chatroom chatroom) {
        this.chatroomName = chatroom.getChatroomName();
        this.chatroomId = chatroom.getChatroomId();
    }

}
