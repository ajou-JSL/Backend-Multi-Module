package jsl.moum.member_profile.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jsl.moum.auth.domain.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import jsl.moum.moum.team.dto.TeamDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class UpdateRequest{
        @NotEmpty @NotNull
        private String name;

        @NotEmpty @NotNull
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotEmpty @NotNull
        private String username;

        @NotEmpty @NotNull
        private String profileDescription;

        private String profileImageUrl;

        @NotEmpty @NotNull
        private String proficiency;

        @NotEmpty @NotNull
        private String instrument;

        @NotEmpty @NotNull
        private String address;

        public MemberEntity toEntity(){
            return MemberEntity.builder()
                    .username(username)
                    .email(email)
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
        private int id;
        private String name;
        private String username;
        private String profileDescription;
        private String email;
        private String profileImageUrl;
        private String proficiency;
        private String instrument;
        private String address;
        private List<TeamDto.Response> teams = new ArrayList<>();

        public Response(MemberEntity member){
            this.id = member.getId();
            this.name = member.getName();
            this.username = member.getUsername();
            this.profileDescription = member.getProfileDescription();
            this.email = member.getEmail();
            this.profileImageUrl = member.getProfileImageUrl();
            this.proficiency = member.getProficiency();
            this.instrument = member.getInstrument();
            this.address = member.getAddress();
            this.teams = member.getTeams().stream() // TeamMemberEntity -> TeamEntity -> TeamDto.Response 변환
                    .map(TeamMemberEntity::getTeam) // TeamEntity 추출
                    .map(TeamDto.Response::new)    // TeamDto.Response로 변환
                    .collect(Collectors.toList());
        }
    }
}



