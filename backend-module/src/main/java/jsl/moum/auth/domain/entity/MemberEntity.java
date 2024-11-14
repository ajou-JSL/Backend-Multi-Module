package jsl.moum.auth.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.member_profile.dto.ProfileDto;
import jsl.moum.rank.Rank;
import jsl.moum.record.domain.entity.RecordEntity;
import jsl.moum.chatroom.domain.ChatroomMember;
import lombok.*;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import org.springframework.security.core.parameters.P;


import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Size(min = 3, max = 10)
    @Column(name = "user_name", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    //@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$")
    private String password;

    @Column(name = "profile_description", nullable = false)
    private String profileDescription;

    //@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMemberEntity> teams = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordEntity> records;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatroomMember> chatroomMembers = new ArrayList<>();

    // role은 회원가입 시 입력하게 할지?
    // admin, 일반사용자, 일반사용자중에서도 연주자,참여자 뭐 이런거 등등..
    @Column(name = "role", nullable = true)
    private String role;

    @Column(name = "profile_image_url", nullable = true)
    private String profileImageUrl;

    @Column(name = "proficiency", nullable = false)
    private String proficiency;

    @Column(name = "instrument", nullable = false)
    private String instrument;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "exp", nullable = false)
    private Integer exp = 0;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rank tier = Rank.BRONZE;

    public void updateMemberExpAndRank(int newExp) {
        if (this.exp == null) {
            this.exp = 0;
        }
        this.exp = this.exp + this.teams.size() + this.records.size() + newExp;
        this.tier = Rank.getRank(this.exp);
    }


    public void removeTeamFromMember(TeamEntity team) {
        teams.removeIf(teamMemberEntity -> teamMemberEntity.getTeam().equals(team));
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
        record.setMember(null);
    }

    public void updateProfileImage(String newUrl){
        this.profileImageUrl = newUrl;
    }

    public void updateMemberInfo(ProfileDto.UpdateRequest updateRequest){
        this.name = updateRequest.getName();
        this.username = updateRequest.getUsername();
        this.profileDescription = updateRequest.getProfileDescription();
        this.email = updateRequest.getEmail();
        this.proficiency = updateRequest.getProficiency();
        this.instrument = updateRequest.getInstrument();
        this.address = updateRequest.getAddress();
    }

    // 양방향이라 서로간 저장-삭제 신경 써줘야함 : GPT
    public void updateRecords(List<RecordEntity> updatedRecords) {
        // 새로 추가된 RecordEntity들을 추가
        for (RecordEntity updatedRecord : updatedRecords) {
            if (!this.records.contains(updatedRecord)) {
                this.records.add(updatedRecord);  // 새 RecordEntity를 추가
            }
            updatedRecord.setMember(this);  // 각 RecordEntity에 현재 MemberEntity 설정
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
            record.setMember(null);  // 해당 RecordEntity와의 관계 끊기
        }
    }

}
