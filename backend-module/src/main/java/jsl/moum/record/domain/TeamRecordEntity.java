package jsl.moum.record.domain;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.moum.team.domain.TeamEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "team_record")
public class TeamRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "fk_team_id")
    private TeamEntity team;

    @ManyToOne
    @JoinColumn(name = "fk_record_id")
    private RecordEntity record;

}
