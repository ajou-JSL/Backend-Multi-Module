package jsl.moum.backendmodule.moum.moum.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity(name = "moum")
public class MoumEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // todo : 연관관계 다시 생각
//    // member - team 관계처럼 만들면 될듯
//    // team - moum 으로 치환
//    // team - team_moum - moum ( 1:N, M:1 )
//    @OneToMany(mappedBy = "moum", fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
//    private List<MoumTeamEntity> teams = new ArrayList<>();

}
