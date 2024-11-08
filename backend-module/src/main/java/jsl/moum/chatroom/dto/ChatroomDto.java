package jsl.moum.chatroom.dto;

import jsl.moum.chatroom.domain.Chatroom;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class ChatroomDto {

    private int id;
    private String name;
    private int type;
    private int teamId;
    private int leaderId;
    private String lastChat;
    private LocalDateTime lastTimestamp;
    private String fileUrl;


    public ChatroomDto(Chatroom chatroom) {
        this.id = chatroom.getId();
        this.name = chatroom.getName();
        this.type = chatroom.getType();
        this.teamId = chatroom.getTeam().getId();
        this.leaderId = chatroom.getTeam().getLeaderId();
        this.lastChat = chatroom.getLastChat();
        this.lastTimestamp = chatroom.getLastTimestamp();
        this.fileUrl = chatroom.getFileUrl();
    }


}
