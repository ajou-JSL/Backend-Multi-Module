package jsl.moum.moum.team.domain;

import jakarta.persistence.*;
import jsl.moum.record.domain.MemberRecordEntity;
import jsl.moum.record.domain.RecordEntity;
import jsl.moum.record.domain.TeamRecordEntity;
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
@Table(name = "team")
public class TeamEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "leader_id")
    private int leaderId;

    @Column(name = "team_name")
    private String teamname;

    @Column(name = "description")
    private String description;

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMemberEntity> members = new ArrayList<>();

    @OneToMany(mappedBy = "team", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamRecordEntity> records = new ArrayList<>();

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

    public void updateProfileImage(String newUrl){
        this.fileUrl = newUrl;
    }

}