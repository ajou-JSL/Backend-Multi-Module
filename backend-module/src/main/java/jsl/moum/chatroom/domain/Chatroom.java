package jsl.moum.chatroom.domain;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jsl.moum.moum.team.domain.TeamEntity;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "chatroom")
public class Chatroom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "type", nullable = false)
    private int type;

    @OneToOne(optional = true)
    @JoinColumns({
            @JoinColumn(name = "team_id", referencedColumnName = "id"),
            @JoinColumn(name = "leader_id", referencedColumnName = "leader_id")
    })
    private TeamEntity team;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Nullable
    @Column(name = "last_chat", nullable = true)
    private String lastChat;

    @Nullable
    @Column(name = "last_timestamp", nullable = true)
    private LocalDateTime lastTimestamp;

    @Column(name = "file_url", nullable = true)
    private String fileUrl;

    @OneToMany(mappedBy = "chatroom", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatroomMember> chatroomMembers = new ArrayList<>();
}
