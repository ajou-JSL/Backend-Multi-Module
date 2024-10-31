package jsl.moum.backendmodule.chatroom.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@Data
@RequiredArgsConstructor
@Entity
@Table(name = "chatroom")
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "chatroom_name")
    private String chatroomName;

    @Column(name = "chatroom_id")
    private int chatroomId;

    @Column(name = "team_id")
    private int memberId;
}