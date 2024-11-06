package jsl.moum.chatappmodule.chat;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@Document(collection = "chatcollection")
public class Chat {
    @Id
    private String id;

    /**
     * Edit parameters as needed
     */

    private String sender;
    private String receiver;
    private String message;
    private int chatroomId;
    private LocalDateTime timestamp;

    /**
     * Minimize the amount of data sent over the request
     * Hides the sender and roomNum information on request
     */
    @Data
    public static class Request{
        private String receiver;
        private String message;
    }

    @Data
    public static class TestRequest{
        private String sender;
        private String receiver;
        private String message;
    }
}