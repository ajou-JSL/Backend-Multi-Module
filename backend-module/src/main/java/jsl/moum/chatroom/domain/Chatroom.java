package jsl.moum.chatroom.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chatroom")
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "type")
    private int type;

    @Column(name = "team_id")
    private int teamId;

    @Column(name = "leader_id")
    private int leaderId;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_chat")
    private String lastChat;

    @Column(name = "last_timestamp")
    private LocalDateTime lastTimestamp;

}
