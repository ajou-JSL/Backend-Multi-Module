package jsl.moum.report.domain;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.moum.team.domain.TeamEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "team_report")
public class TeamReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Foreign key reference to the team
    @ManyToOne
    @JoinColumn(name = "team_id", referencedColumnName = "id", nullable = false)
    private TeamEntity team;

    // Foreign key reference to the reporter
    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "id", nullable = false)
    private MemberEntity reporter;

    @Column(name = "type")
    private String type;

    @Column(name = "details")
    private String details;

    @Column(name = "reply")
    private String reply;

    @Column(name = "is_resolved", columnDefinition = "boolean default false")
    private boolean isResolved;
}
