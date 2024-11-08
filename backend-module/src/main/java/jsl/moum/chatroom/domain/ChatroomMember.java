package jsl.moum.chatroom.domain;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chatroom_member")
public class ChatroomMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "chatroom_id")
    private Chatroom chatroom;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

}
