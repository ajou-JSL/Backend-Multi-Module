package jsl.moum.record.domain.entity;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.moum.lifecycle.domain.LifecycleEntity;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.record.domain.dto.RecordDto;
import lombok.*;

import java.util.Date;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "record")
public class RecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "record_name")
    private String recordName;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "end_date")
    private Date endDate;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "fk_team_id")
    private TeamEntity team;

    @ManyToOne
    @JoinColumn(name = "fk_lifecycle_id")
    private LifecycleEntity lifecycle;

    public void updateRecord(RecordDto.Request request) {
        this.recordName = request.getRecordName();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
    }

}