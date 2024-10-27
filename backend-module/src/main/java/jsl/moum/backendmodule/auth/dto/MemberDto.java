package jsl.moum.backendmodule.auth.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import jsl.moum.backendmodule.auth.domain.entity.Address;
import jsl.moum.backendmodule.auth.domain.entity.MemberEntity;

public class MemberDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Request{
        private int id;

        @NotEmpty @NotNull
        @Size(min=3, max=10)
        private String username;

        @NotEmpty @NotNull
        private String password;

        @NotEmpty @NotNull @Email
        private String email;
        private String description;

        private String verifyCode;

        // todo : 필수항목으로 바꿀거임
        private Address address;
        private String profileImageUrl;

        // private String verifyCode;

        public MemberEntity toEntity(){
            return MemberEntity.builder()
                    .id(id)
                    .username(username)
                    .email(email)
                    .password(password)
                    .address(address)
                    .profileImageUrl(profileImageUrl)
                    .description(description)
                    .build();
        }
    }

    @Getter
    public static class Response{
        private final int id;
        private final String username;
        private final String description;
        private final String profileImageUrl;

        public Response(MemberEntity member){
            this.id = member.getId();
            this.username = member.getUsername();
            this.description = member.getDescription();
            this.profileImageUrl = member.getProfileImageUrl();
        }
    }

}
