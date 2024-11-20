package jsl.moum.auth.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.community.perform.domain.entity.PerformMember;
import jsl.moum.member_profile.dto.ProfileDto;
import jsl.moum.rank.Rank;
import jsl.moum.record.domain.entity.MoumMemberRecordEntity;
import jsl.moum.record.domain.entity.RecordEntity;
import jsl.moum.chatroom.domain.ChatroomMember;
import jsl.moum.report.domain.MemberReport;
import lombok.*;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Slf4j
@Table(name = "member")
public class MemberEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "name", nullable = false)
    private String name;

    @Size(min = 3, max = 20)
    @Column(name = "user_name", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    //@Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$")
    private String password;

    @Column(name = "profile_description", nullable = true)
    private String profileDescription;

    @Column(name = "video_url", nullable = true)
    private String videoUrl;

    //@Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamMemberEntity> teams = new ArrayList<>();

    // 개인 이력
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecordEntity> records = new ArrayList<>();

    // 모음 이력
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MoumMemberRecordEntity> moumMemberRecords;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<ChatroomMember> chatroomMembers = new ArrayList<>();

    // 멤버가 참여한 공연게시글들
    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformMember> membersPerforms = new ArrayList<>();

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberReport> memberReports = new ArrayList<>();

    @OneToMany(mappedBy = "reporter", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberReport> memberReporters = new ArrayList<>();

    @ElementCollection(targetClass = MusicGenre.class)
    @CollectionTable(name = "member_genre", joinColumns = @JoinColumn(name = "member_id"))
    @Column(name = "genre", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<MusicGenre> genres = new ArrayList<>();

    // role은 회원가입 시 입력하게 할지?
    // admin, 일반사용자, 일반사용자중에서도 연주자,참여자 뭐 이런거 등등..
    @Column(name = "role", nullable = false)
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
    @Column(name = "tier", nullable = false)
    private Rank tier = Rank.BRONZE;

    @Transient
    private int totalRecordCount;

    public void updateMemberExpAndRank(int newExp) {
        if (this.exp == null) {
            this.exp = 0;
        }
        //this.exp = this.exp + this.teams.size() + this.records.size() + newExp;
        this.exp += newExp;
        this.tier = Rank.getRank(this.exp);
        log.info("======= updateMemberExpAndRank() tier,exp:{}, {}",tier, exp);
    }


    public void removeTeamFromMember(TeamEntity team) {
        teams.removeIf(teamMemberEntity -> teamMemberEntity.getTeam().equals(team));
    }

    public void assignRecord(List<RecordEntity> records) {
        log.info("assign Record method called");
        if (this.records == null) {
            this.records = new ArrayList<>();
        }

        if (records == null || records.isEmpty()) {
            return;
        }

        for (RecordEntity record : records) {
            record.setMember(this);
        }

        this.records.addAll(records);
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

    public void updateMemberInfo(ProfileDto.UpdateRequest updateRequest) {
        if (updateRequest.getName() != null) {
            this.name = updateRequest.getName();
        }
        if (updateRequest.getUsername() != null) {
            this.username = updateRequest.getUsername();
        }
        if (updateRequest.getProfileDescription() != null) {
            this.profileDescription = updateRequest.getProfileDescription();
        }
        if (updateRequest.getEmail() != null) {
            this.email = updateRequest.getEmail();
        }
        if (updateRequest.getProficiency() != null) {
            this.proficiency = updateRequest.getProficiency();
        }
        if (updateRequest.getInstrument() != null) {
            this.instrument = updateRequest.getInstrument();
        }
        if (updateRequest.getAddress() != null) {
            this.address = updateRequest.getAddress();
        }
        if (updateRequest.getGenres() != null) {
            this.genres = updateRequest.getGenres();
        }
    }


    // 양방향이라 서로간 저장-삭제 신경 써줘야함 : GPT
    public void updateRecords(List<RecordEntity> updatedRecords) {
        // 새로 추가된 RecordEntity들을 추가
        for (RecordEntity updatedRecord : updatedRecords) {
            if (!this.records.contains(updatedRecord)) {
                this.records.add(updatedRecord);  // 새 RecordEntity를 추가
            }
            updatedRecord.setMember(this);  // 각 RecordEntity에 현재 TeamEntity 설정
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
            record.setMember(null);  // 해당 RecordEntity와의 팀 관계 끊기
        }
    }

    public List<SimpleGrantedAuthority> getAuthorities(){
        List<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(this.role));
        return authorities;
    }

}
