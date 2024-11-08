package jsl.moum.chatappmodule.chat;


import jakarta.annotation.Nullable;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "chatroom")
public class Chatroom {

    @Id
    @Column
    private int id;

    @Column
    private String name;

    @Column
    private int type;

    @Column("team_id")
    private int teamId;

    @Column("leader_id")
    private int leaderId;

    @Column("created_at")
    private LocalDateTime createdAt;

    @Column("last_chat")
    private String lastChat;

    @Column("last_timestamp")
    private LocalDateTime lastTimestamp;

    @Column("file_url")
    private String fileUrl;
}
