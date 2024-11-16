package jsl.moum.member_profile.dto;

import jakarta.annotation.Nullable;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.rank.Rank;
import jsl.moum.record.domain.dto.RecordDto;
import jsl.moum.record.domain.entity.MoumMemberRecordEntity;
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
        @Size(min = 3, max = 20)
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

        @Nullable
        private List<RecordDto.Request> records;
        private String videoUrl;

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
                    .videoUrl(videoUrl)
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
        private String videoUrl;
        private String proficiency;
        private String instrument;
        private String address;
        private Integer exp;
        private Rank tier;
        private List<TeamDto.Response> teams;
        private List<RecordDto.Response> memberRecords;
        private List<RecordDto.Response> moumRecords;

        public Response(MemberEntity member){
            this.id = member.getId();
            this.name = member.getName();
            this.username = member.getUsername();
            this.profileDescription = member.getProfileDescription();
            this.email = member.getEmail();
            this.profileImageUrl = member.getProfileImageUrl();
            this.videoUrl = member.getVideoUrl();
            this.proficiency = member.getProficiency();
            this.instrument = member.getInstrument();
            this.address = member.getAddress();
            this.exp = member.getExp();
            this.tier = member.getTier();

            this.teams = (member.getTeams() != null) ? member.getTeams().stream()
                    .map(TeamMemberEntity::getTeam)
                    .map(TeamDto.Response::new)
                    .collect(Collectors.toList())
                    : null;

            this.memberRecords = (member.getRecords() != null)
                    ? member.getRecords().stream()
                    .map(RecordDto.Response::new)
                    .collect(Collectors.toList())
                    : null;


            this.moumRecords = (member.getMoumMemberRecords() != null)
                    ? member.getMoumMemberRecords().stream()
                    .map(MoumMemberRecordEntity::getRecord)
                    .map(RecordDto.Response::new)
                    .collect(Collectors.toList())
                    : null;
        }
    }
}



