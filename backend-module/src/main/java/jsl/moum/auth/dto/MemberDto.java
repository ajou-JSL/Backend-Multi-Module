package jsl.moum.auth.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.rank.Rank;
import jsl.moum.record.domain.dto.RecordDto;
import jsl.moum.record.domain.entity.MoumMemberRecordEntity;
import jsl.moum.record.domain.entity.RecordEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MemberDto {

    @Builder
    @AllArgsConstructor
    @Getter
    public static class Request{
        private int id;

        @NotEmpty @NotNull
        private String name;

        @NotEmpty @NotNull
        @Size(min=3, max=10)
        private String username;

        @NotEmpty @NotNull(message = "비밀번호를 입력해주세요")
        @Pattern(regexp = "^(?=.*[a-zA-Z])(?=.*[0-9])(?=.*[!@#$%^&*?_]).{8,20}$", message = "비밀번호 형식에 맞게 입력해주세요")
        private String password;

        @NotEmpty @NotNull(message = "이메일을 입력해주세요") @Email
        @Pattern(regexp = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$", message = "이메일 형식이 올바르지 않습니다.")
        private String email;

        @NotEmpty @NotNull(message = "프로필 설명을 입력해주세요")
        private String profileDescription;

        @NotEmpty @NotNull(message = "인증코드를 입력해주세요")
        @Pattern(regexp = "^[0-9a-zA-Z]{6}$")
        private String verifyCode;

        @NotEmpty @NotNull(message = "주소를 입력해주세요")
        private String address;
        private String profileImageUrl;

        @NotEmpty @NotNull(message = "악기 숙련도를 입력해주세요")
        private String proficiency;

        @NotEmpty @NotNull(message = "사용 악기를 입력해주세요")
        private String instrument;

        @Nullable
        private List<RecordDto.Request> records;
        private String videoUrl;



        // private String verifyCode;

        public MemberEntity toEntity(){
            return MemberEntity.builder()
                    .id(id)
                    .username(username)
                    .email(email)
                    .password(password)
                    .address(address)
                    .videoUrl(videoUrl)
                    .profileImageUrl(profileImageUrl)
                    .profileDescription(profileDescription)
                    .instrument(instrument)
                    .proficiency(proficiency)
                    .name(name)
                    .records(records.stream().map(RecordDto.Request::toEntity).collect(Collectors.toList()))
                    .exp(0)
                    .tier(Rank.BRONZE)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int id;
        private final String name;
        private final String username;
        private final String profileDescription;
        private final String profileImageUrl;
        private final Integer exp;
        private final Rank tier;
        private final String videoUrl;
        private final List<RecordDto.Response> memberRecords;
        private final List<RecordDto.Response> moumRecords;


        public Response(MemberEntity member){
            this.id = member.getId();
            this.name = member.getName();
            this.username = member.getUsername();
            this.profileDescription = member.getProfileDescription();
            this.profileImageUrl = member.getProfileImageUrl();
            this.videoUrl = member.getVideoUrl();
            this.exp = member.getExp();
            this.tier = member.getTier();
//            this.memberRecords = member.getRecords().stream()
//                    .map(RecordDto.Response::new)
//                    .collect(Collectors.toList());
//            this.moumRecords = member.getMoumMemberRecords().stream()
//                    .map(MoumMemberRecordEntity::getRecord)
//                    .map(RecordDto.Response::new)
//                    .collect(Collectors.toList());

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
