package jsl.moum.chatappmodule.auth.domain.entity;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.MatchesPattern;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Table(name = "member")
public class MemberEntity {

    @Id
    @Column
    private int id;

    @Column
    private String name;

    @Column
    private String username;

    @Column
    private String password;

    @Column
    private String profileDescription;

    @Column
    private String email;

    // role은 회원가입 시 입력하게 할지?
    // admin, 일반사용자, 일반사용자중에서도 연주자,참여자 뭐 이런거 등등..
    @Column
    private String role;

    @Column
    private String profileImageUrl;

    @Column
    private String proficiency;

    @Column
    private String instrument;

    @Column
    private String address;

}
