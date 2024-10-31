package jsl.moum.backendmodule.moum.team.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "team")
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "leader_id")
    private int leaderId;

    @Column(name = "team_name")
    private String teamname;

    @Column(name = "description")
    private String description;

    @Column(name = "chatroom_id")
    private Long chatroomId;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TeamMemberEntity> members = new ArrayList<>();

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @PrePersist
    public void createDate(){
        this.createdAt = LocalDateTime.now();
    }

    public void removeMemberFromTeam(TeamMemberEntity teamMember) {
        this.members.remove(teamMember);
    }
}