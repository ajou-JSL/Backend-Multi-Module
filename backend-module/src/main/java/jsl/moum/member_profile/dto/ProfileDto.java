package jsl.moum.member_profile.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.rank.Rank;
import jsl.moum.record.domain.dto.RecordDto;
import jsl.moum.record.domain.entity.RecordEntity;
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

        private List<RecordDto.Request> records;

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
                    .records(records.stream().map(RecordDto.Request::toEntity).collect(Collectors.toList()))
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
        private Integer exp;
        private Rank tier;
        private List<TeamDto.Response> teams;
        private List<RecordDto.Response> records;

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
            this.exp = member.getExp();
            this.tier = member.getTier();
            this.teams = member.getTeams().stream() // TeamMemberEntity -> TeamEntity -> TeamDto.Response 변환
                    .map(TeamMemberEntity::getTeam) // TeamEntity 추출
                    .map(TeamDto.Response::new)    // TeamDto.Response로 변환
                    .collect(Collectors.toList());
            this.records = member.getRecords().stream()
                    .map(RecordDto.Response::new)
                    .collect(Collectors.toList());;
        }
    }
}



