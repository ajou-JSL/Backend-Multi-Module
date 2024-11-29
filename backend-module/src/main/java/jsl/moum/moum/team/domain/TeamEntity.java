package jsl.moum.moum.team.domain;

import jakarta.persistence.*;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.moum.team.dto.TeamDto;
import jsl.moum.rank.Rank;
import jsl.moum.record.domain.entity.RecordEntity;
import jsl.moum.chatroom.domain.Chatroom;
import jsl.moum.report.domain.TeamReport;
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
@Table(
        name = "team",
        indexes = {
                @Index(name = "idx_team_leader_id", columnList = "leader_id")
        }
)
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "leader_id")
    private int leaderId;

    @Column(name = "team_name", unique = true)
    private String teamName;

    @Column(name = "description")
    private String description;

    @Column(name = "genre")
    @Enumerated(EnumType.STRING)
    private MusicGenre genre;

    @Column(name = "location")
    private String location;

    // private 이력

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMemberEntity> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordEntity> records;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamReport> teamReports = new ArrayList<>();

    @OneToOne(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Chatroom chatroom;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "file_url")
    private String fileUrl;

    @Column(name = "video_url")
    private String videoUrl;

    @Column(name = "exp")
    private Integer exp = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private Rank tier = Rank.BRONZE;

    public void updateTeamExpAndRank(int newExp) {
        if (this.exp == null) {
            this.exp = 0;
        }
        //this.exp = this.exp + this.members.size() + this.records.size() + newExp;
        this.exp += newExp;
        this.tier = Rank.getRank(this.exp);
    }

    @PrePersist
    public void createDate(){
        this.createdAt = LocalDateTime.now();
    }

    public void assignRecord(RecordEntity record){
        if(record == null){
            return;
        }
        this.records.add(record);
    }

    public void removeRecord(RecordEntity record){
        if(record == null){
            return;
        }
        this.records.remove(record);
        record.setTeam(null);
    }

    public void removeMemberFromTeam(TeamMemberEntity teamMember) {
        this.members.remove(teamMember);
    }

    public void updateTeamInfo(TeamDto.UpdateRequest requestDto) {
        if (requestDto != null) {
            updateTeamName(requestDto.getTeamName());
            updateDescription(requestDto.getDescription());
            updateGenre(requestDto.getGenre());
            updateLocation(requestDto.getLocation());
            updateVideoUrl(requestDto.getVideoUrl());
        }
    }

    private void updateTeamName(String teamName) {
        if (teamName != null) {
            this.teamName = teamName;
        }
    }

    private void updateDescription(String description) {
        if (description != null) {
            this.description = description;
        }
    }

    private void updateGenre(MusicGenre genre) {
        if (genre != null) {
            this.genre = genre;
        }
    }

    private void updateLocation(String location) {
        if (location != null) {
            this.location = location;
        }
    }

    private void updateVideoUrl(String videoUrl) {
        if (videoUrl != null) {
            this.videoUrl = videoUrl;
        }
    }


    public void updateProfileImage(String newUrl){
        this.fileUrl = newUrl;
    }

    // 양방향이라 서로간 저장-삭제 신경 써줘야함 : GPT
    public void updateRecords(List<RecordEntity> updatedRecords) {
        // 새로 추가된 RecordEntity들을 추가
        for (RecordEntity updatedRecord : updatedRecords) {
            if (!this.records.contains(updatedRecord)) {
                this.records.add(updatedRecord);  // 새 RecordEntity를 추가
            }
            updatedRecord.setTeam(this);  // 각 RecordEntity에 현재 TeamEntity 설정
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
            record.setTeam(null);  // 해당 RecordEntity와의 팀 관계 끊기
        }
    }
}