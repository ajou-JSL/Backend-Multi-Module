package study.moum.auth.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import study.moum.moum.team.domain.TeamEntity;
import study.moum.moum.team.domain.TeamMemberEntity;
import study.moum.record.domain.MemberRecordEntity;
import study.moum.record.domain.RecordEntity;

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
    @Pattern(regexp = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{2,10}$")
    private String name;

    @Size(min = 3, max = 10)
    @Column(name = "user_name", nullable = false)
    private String username;

    @Column(name = "password", nullable = false)
    @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$")
    private String password;

    @Column(name = "profile_description", nullable = true)
    private String profileDescription;

    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TeamMemberEntity> teams = new ArrayList<>();

    // role은 회원가입 시 입력하게 할지?
    // admin, 일반사용자, 일반사용자중에서도 연주자,참여자 뭐 이런거 등등..
    @Column(name = "role", nullable = true)
    private String role;

    @Column(name = "profile_image_url", nullable = true)
    private String profileImageUrl;

    @Column(name = "proficiency", nullable = true)
    private String proficiency;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MemberRecordEntity> records = new ArrayList<>();

    @Column(name = "instrument", nullable = true)
    private String instrument;

    // todo : 필수항목으로
    @Column(name = "address", nullable = true)
    private String address;

    public void removeTeamFromMember(TeamEntity team) {
        teams.removeIf(teamMemberEntity -> teamMemberEntity.getTeam().equals(team));
    }

    public void removeRecordFromMember(RecordEntity record) {
        records.removeIf(memberRecordEntity -> memberRecordEntity.getRecord().equals(record));
    }

    public void updateProfileImage(String newUrl){
        this.profileImageUrl = newUrl;
    }

    public void updateMemberInfo(       String name,
                                        String username,
                                        String profileDescription,
                                        String email,
                                        String proficiency,
                                        String instrument,
                                        String address){

        this.name = name;
        this.username = username;
        this.profileDescription = profileDescription;
        this.email = email;
        this.proficiency = proficiency;
        this.instrument = instrument;
        this.address = address;
    }
}
