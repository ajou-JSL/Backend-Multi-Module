package jsl.moum.chatappmodule.auth.dto;

import io.asyncer.r2dbc.mysql.internal.NotNullByDefault;
import jsl.moum.chatappmodule.auth.domain.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

public class MemberDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Request{
        private int id;

        private String name;

        private String username;

        private String password;

        private String email;

        private String profileDescription;

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
