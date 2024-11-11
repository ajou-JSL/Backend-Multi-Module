package jsl.moum.moum.team.domain;

import jakarta.persistence.*;
import jsl.moum.moum.team.dto.TeamDto;
import jsl.moum.record.domain.entity.RecordEntity;
import jsl.moum.chatroom.domain.Chatroom;
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

    @Column(name = "team_name")
    private String teamName;

    @Column(name = "description")
    private String description;

    @Column(name = "genre")
    private String genre;

    @Column(name = "location")
    private String location;

    // private 이력

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMemberEntity> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordEntity> records;

    @OneToOne(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private Chatroom chatroom;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "file_url")
    private String fileUrl;

    @PrePersist
    public void createDate(){
        this.createdAt = LocalDateTime.now();
    }

    public void removeMemberFromTeam(TeamMemberEntity teamMember) {
        this.members.remove(teamMember);
    }

    public void updateTeamInfo(TeamDto.UpdateRequest requestDto) {
        this.teamName = requestDto.getTeamName();
        this.description = requestDto.getDescription();
        this.genre = requestDto.getGenre();
        this.location = requestDto.getLocation();
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