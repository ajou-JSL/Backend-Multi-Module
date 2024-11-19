package jsl.moum.admin.dto;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public class AdminLoginRequest {

    String username;
    String password;

}
