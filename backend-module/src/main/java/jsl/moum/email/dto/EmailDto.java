package jsl.moum.email.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

public class EmailDto {

    @Getter
    @Setter
    public static class Request{
        @Pattern(regexp = "^[\\w!#$%&'*+/=?`{|}~^.-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$", message = "이메일 형식이 올바르지 않습니다.")
        @NotEmpty(message = "이메일 입력을 입력하세요")
        private String email;
    }
}
