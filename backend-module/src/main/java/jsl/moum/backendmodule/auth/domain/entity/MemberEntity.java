package jsl.moum.backendmodule.auth.domain.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import jsl.moum.backendmodule.moum.team.domain.TeamEntity;
import jsl.moum.backendmodule.moum.team.domain.TeamMemberEntity;
import lombok.*;


import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "member")
public class MemberEntity { // todo : userdetails implement 여기다가 + db column 에 안넣게하기

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @NotEmpty
    @NotNull
    @Size(min = 3, max = 10)
    @Column(name = "user_name", nullable = false)
    private String username;

    @NotEmpty
    @NotNull
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "description", nullable = true)
    private String description;

    @NotEmpty
    @NotNull
    @Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "member", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private List<TeamMemberEntity> teams = new ArrayList<>();

    // role은 회원가입 시 입력하게 할지?
    // admin, 일반사용자, 일반사용자중에서도 연주자,참여자 뭐 이런거 등등..
    @Column(name = "role", nullable = true)
    private String role;

    @Column(name = "profile_image_url")
    private String profileImageUrl;

    @Embedded
    // todo : 필수항목으로 바꿀거임
    private Address address;

    public void removeTeamFromMember(TeamEntity team) {
        teams.removeIf(teamMemberEntity -> teamMemberEntity.getTeam().equals(team));
    }

}
