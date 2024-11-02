package jsl.moum.moum.team.domain;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "team_member")
public class TeamMemberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "leader_id", nullable = false)
    private int leaderId;

    @ManyToOne
    @JoinColumn(name = "member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "team_id")
    private TeamEntity team;
}
