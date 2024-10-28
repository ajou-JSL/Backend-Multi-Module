package jsl.moum.chatappmodule.chat;


import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ChatDto{
    private String sender;
    private String receiver;
    private String message;
    private int chatroomId;
    private LocalDateTime timestamp;

    public ChatDto(Chat chat){
        this.sender = chat.getSender();
        this.receiver = chat.getReceiver();
        this.message = chat.getMessage();
        this.chatroomId = chat.getChatroomId();
        this.timestamp = chat.getTimestamp();
    }
}
