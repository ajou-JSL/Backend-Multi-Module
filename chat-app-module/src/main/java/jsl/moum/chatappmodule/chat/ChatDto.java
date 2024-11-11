package jsl.moum.chatappmodule.chat;


import lombok.Data;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class ChatDto{
    private String sender;
    private String message;
    private int chatroomId;
    private LocalDateTime timestamp;
    private String timestampFormatted;

    public ChatDto(Chat chat){
        this.sender = chat.getSender();
        this.message = chat.getMessage();
        this.chatroomId = chat.getChatroomId();
        this.timestamp = chat.getTimestamp();
        this.timestampFormatted = chat.getTimestamp().format(DateTimeFormatter.ofPattern("yyyy-MM-dd h:mm a"));
    }
}
