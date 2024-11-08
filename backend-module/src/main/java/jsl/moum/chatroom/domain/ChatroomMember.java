package jsl.moum.chatroom.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chatroom_member")
public class ChatroomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


}
