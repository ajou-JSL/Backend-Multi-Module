package jsl.moum.chatappmodule.auth.domain.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@Table(name = "refresh_token")
public class RefreshEntity {

    @Id
    @Column
    private Long id;

    @Column
    private String username;

    @Column
    private String refresh;

    @Column
    private String expiration;
}