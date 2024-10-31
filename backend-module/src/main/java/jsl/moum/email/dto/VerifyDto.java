package jsl.moum.email.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

public class VerifyDto {

    @Getter
    @Setter
    public static class Request{
        @NotNull @NotEmpty(message = "인증 코드를 입력해야합니다.")
        @NotBlank(message = "인증 코드를 입력하세요")
        private String verifyCode;

        @NotNull @NotEmpty(message = "인증 코드를 입력해야합니다.")
        @NotBlank(message = "인증 코드를 입력하세요")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;

    }

    @Getter
    @Setter
    public static class Response{
        private String verifyCode;
        private Boolean result;
    }
}
