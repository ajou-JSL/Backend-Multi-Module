package study.moum.auth.dto;


import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import study.moum.auth.domain.entity.MemberEntity;

public class MemberDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Request{
        private int id;

        @NotEmpty @NotNull
        @Pattern(regexp = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{2,10}$")
        private String name;

        @NotEmpty @NotNull
        @Size(min=3, max=10)
        private String username;

        @NotEmpty @NotNull
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$")
        private String password;

        @NotEmpty @NotNull @Email
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        private String profileDescription;

        @NotEmpty @NotNull
        @Pattern(regexp = "^[0-9a-zA-Z]{6}$")
        private String verifyCode;

        // todo : 필수항목으로 바꿀거임
        private String address;
        private String profileImageUrl;

        private String proficiency;
        private String instrument;

        // private String verifyCode;

        public MemberEntity toEntity(){
            return MemberEntity.builder()
                    .id(id)
                    .username(username)
                    .email(email)
                    .password(password)
                    .address(address)
                    .profileImageUrl(profileImageUrl)
                    .profileDescription(profileDescription)
                    .instrument(instrument)
                    .proficiency(proficiency)
                    .name(name)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int id;
        private final String username;
        private final String profileDescription;
        private final String profileImageUrl;

        public Response(MemberEntity member){
            this.id = member.getId();
            this.username = member.getUsername();
            this.profileDescription = member.getProfileDescription();
            this.profileImageUrl = member.getProfileImageUrl();
        }
    }

}
