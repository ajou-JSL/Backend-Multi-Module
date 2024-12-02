package jsl.moum.chatroom.dto;

import jsl.moum.chatroom.domain.Chatroom;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatroomDto {

    private int id;
    private String name;
    private int type;
    private Integer teamId;
    private Integer leaderId;
    private String lastChat;
    private LocalDateTime lastTimestamp;
    private String fileUrl;


    public ChatroomDto(Chatroom chatroom) {
        this.id = chatroom.getId();
        this.name = chatroom.getName();
        this.type = chatroom.getType();
        if(chatroom.getTeam() == null){
            this.teamId = null;
            this.leaderId = null;
        } else{
            this.teamId = chatroom.getTeam().getId();
            this.leaderId = chatroom.getTeam().getLeaderId();
        }
        this.lastChat = chatroom.getLastChat();
        this.lastTimestamp = chatroom.getLastTimestamp();
        this.fileUrl = chatroom.getFileUrl();
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Request{
        private String name;
        private int type;
        private Integer teamId;
        private Integer leaderId;
        private List<Integer> members;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Patch{
        private String name;
    }

    @Getter
    @Setter
    @RequiredArgsConstructor
    public static class Members {
        private int type;
        private List<Integer> memberIds;
    }
}
