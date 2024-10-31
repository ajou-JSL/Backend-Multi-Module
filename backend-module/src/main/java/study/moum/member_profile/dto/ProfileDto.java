package study.moum.member_profile.dto;

import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.moum.team.domain.TeamMemberEntity;
import study.moum.moum.team.dto.TeamDto;
import study.moum.record.domain.MemberRecordEntity;
import study.moum.record.dto.RecordDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class UpdateRequest{
        @Pattern(regexp = "^[a-zA-Z0-9가-힣ㄱ-ㅎㅏ-ㅣ]{2,10}$")
        private String name;

        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        private String username;
        private String profileDescription;

        private String profileImageUrl;
        private String proficiency;
        private String instrument;
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
        private List<TeamDto.Response> teams = new ArrayList<>();
        private String profileImageUrl;
        private String proficiency;
        private List<RecordDto.Response> records = new ArrayList<>();
        private String instrument;
        private String address;

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
            this.records = member.getRecords().stream() // MemberRecordEntity -> RecordEntity -> RecordDto.Response 변환
                    .map(MemberRecordEntity::getRecord) // RecordEntity 추출
                    .map(RecordDto.Response::new)       // RecordDto.Response로 변환
                    .collect(Collectors.toList());
        }
    }
}



