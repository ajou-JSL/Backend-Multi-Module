package jsl.moum.moum.lifecycle.domain;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.moum.lifecycle.dto.LifecycleDto;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import jsl.moum.record.domain.entity.RecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity(name = "lifecycle")
public class LifecycleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "leader_id")
    private int leaderId;

    @Column(name = "leader_name")
    private String leaderName;

    @Column(name = "lifecycle_name")
    private String lifecycleName;

    @Column(name = "lifecycle_description")
    private String lifecycleDescription;

    @Column(name = "peform_location")
    private String performLocation;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "price")
    private int price;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "fk_team_id")
    private TeamEntity team;

    @Embedded
    private Process process;

    @OneToMany(mappedBy = "lifecycle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordEntity> records;

    public void assignTeam(TeamEntity team) {
        if (team != null) {
            this.team = team;
        }
    }

    public void updateProfileImage(String newUrl){
        this.imageUrl = newUrl;
    }

    public void updateLifecycleInfo(LifecycleDto.Request updateRequest){
        this.lifecycleName = updateRequest.getMoumName();
        this.price = updateRequest.getPrice();
        this.lifecycleDescription = updateRequest.getMoumDescription();
        this.performLocation = updateRequest.getPerformLocation();
        this.endDate = updateRequest.getEndDate();
    }

    // 양방향이라 서로간 저장-삭제 신경 써줘야함 : GPT
    public void updateRecords(List<RecordEntity> updatedRecords) {
        // 새로 추가된 RecordEntity들을 추가
        for (RecordEntity updatedRecord : updatedRecords) {
            if (!this.records.contains(updatedRecord)) {
                this.records.add(updatedRecord);  // 새 RecordEntity를 추가
            }
            updatedRecord.setLifecycle(this);  // 각 RecordEntity에 현재 TeamEntity 설정
        }

        // 기존 records에서 업데이트된 목록에 포함되지 않는 RecordEntity를 삭제
        List<RecordEntity> toRemove = new ArrayList<>();
        for (RecordEntity existingRecord : this.records) {
            if (!updatedRecords.contains(existingRecord)) {
                toRemove.add(existingRecord);
            }
        }

        // 삭제할 RecordEntity들을 리스트에서 제거
        this.records.removeAll(toRemove);

        // toRemove에 추가된 RecordEntity들은 실제로 삭제되도록 처리
        for (RecordEntity record : toRemove) {
            record.setLifecycle(null);  // 해당 RecordEntity와의 팀 관계 끊기
        }
    }

}