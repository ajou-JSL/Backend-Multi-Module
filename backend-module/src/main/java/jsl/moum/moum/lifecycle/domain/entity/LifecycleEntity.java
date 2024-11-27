package jsl.moum.moum.lifecycle.domain.entity;

import jakarta.persistence.*;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.moum.lifecycle.domain.Music;
import jsl.moum.moum.lifecycle.domain.Process;
import jsl.moum.moum.lifecycle.dto.LifecycleDto;
import jsl.moum.moum.team.domain.TeamEntity;

import jsl.moum.record.domain.entity.MoumMemberRecordEntity;
import jsl.moum.record.domain.entity.RecordEntity;
import lombok.*;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    private Integer price;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lifecycle_images", joinColumns = @JoinColumn(name = "lifecycle_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Column(name = "genre", nullable = false)
    @Enumerated(EnumType.STRING)
    private MusicGenre genre;

    @ManyToOne
    @JoinColumn(name = "fk_team_id")
    private TeamEntity team;

    @Embedded
    private Process process;

    @Column(name = "fk_settlement_id")
    private Integer settlementId;

    // todo : 이거 수정하기
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lifecycle_music_list", joinColumns = @JoinColumn(name = "lifecycle_id"))
    private List<Music> music;

    @OneToMany(mappedBy = "lifecycle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordEntity> records;

    @OneToMany(mappedBy = "lifecycle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MoumMemberRecordEntity> moumMemberRecords;

    @OneToMany(mappedBy = "moum", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LifecyclePracticeRoom> lifecyclePracticeRooms;

    @OneToMany(mappedBy = "moum", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LifecyclePerformanceHall> lifecyclePerformanceHalls;

    public void assignTeam(TeamEntity team) {
        if (team != null) {
            this.team = team;
        }
    }

    public void assignProcess(Process process){
        if(process != null){
            this.process = process;
        }else{
            this.process = new Process();
        }
    }

    public void removeTeam(TeamEntity targetTeam) {
        if (this.team != null && this.team.equals(targetTeam)) {
            this.team = null;
        }
    }
    public void updateProfileImages(List<String> newUrls) {
        if(this.imageUrls == null){
            this.imageUrls = new ArrayList<>();
        }
        this.imageUrls.clear();
        this.imageUrls.addAll(newUrls);
    }
    public void updateLifecycleInfo(LifecycleDto.Request updateRequest) {
        if (updateRequest != null) {
            updateGenre(updateRequest.getGenre());
            updateLifecycleName(updateRequest.getMoumName());
            updatePrice(updateRequest.getPrice());
            updateLifecycleDescription(updateRequest.getMoumDescription());
            updatePerformLocation(updateRequest.getPerformLocation());
            updateEndDate(updateRequest.getEndDate());
            updateMusic(updateRequest.getMusic());
        }
    }

    private void updateGenre(MusicGenre genre) {
        if (genre != null) {
            this.genre = genre;
        }
    }

    private void updateLifecycleName(String lifecycleName) {
        if (lifecycleName != null) {
            this.lifecycleName = lifecycleName;
        }
    }

    private void updatePrice(Integer price) {
        if (price != null) {
            this.price = price;
        }
    }

    private void updateLifecycleDescription(String lifecycleDescription) {
        if (lifecycleDescription != null) {
            this.lifecycleDescription = lifecycleDescription;
        }
    }

    private void updatePerformLocation(String performLocation) {
        if (performLocation != null) {
            this.performLocation = performLocation;
        }
    }

    private void updateEndDate(LocalDate endDate) {
        if (endDate != null) {
            this.endDate = endDate;
        }
    }

    private void updateMusic(List<Music> music) {
        if (music != null) {
            this.music = music;
        }
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
