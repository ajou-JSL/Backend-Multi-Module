package jsl.moum.record.domain;

import jakarta.persistence.*;
import jsl.moum.moum.team.domain.TeamEntity;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "record")
public class RecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = " record_name")
    private String recordName;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(mappedBy = "record", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<MemberRecordEntity> members = new ArrayList<>();

    @OneToMany(mappedBy = "record", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TeamRecordEntity> teams = new ArrayList<>();

    @PrePersist
    public void createDate(){
        this.startDate = LocalDate.now();
    }

    public void updateTeamRecords(String recordName, LocalDate startDate, LocalDate endDate){
        this.recordName = recordName;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
