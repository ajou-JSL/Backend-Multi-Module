package jsl.moum.record.domain.entity;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.record.domain.dto.RecordDto;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

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
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne
    @JoinColumn(name = "fk_member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "fk_team_id")
    private TeamEntity team;

    @ManyToOne
    @JoinColumn(name = "fk_lifecycle_id")
    private LifecycleEntity lifecycle;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "record", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MoumMemberRecordEntity> moumMemberRecords;

    @PrePersist
    void createdAt(){
        this.createdAt = LocalDateTime.now();
    }

    public void updateRecord(RecordDto.Request request) {
        if (request != null) {
            updateRecordName(request.getRecordName());
            updateStartDate(request.getStartDate());
            updateEndDate(request.getEndDate());
        }
    }

    private void updateRecordName(String recordName) {
        if (recordName != null) {
            this.recordName = recordName;
        }
    }

    private void updateStartDate(LocalDate startDate) {
        if (startDate != null) {
            this.startDate = startDate;
        }
    }

    private void updateEndDate(LocalDate endDate) {
        if (endDate != null) {
            this.endDate = endDate;
        }
    }

}
