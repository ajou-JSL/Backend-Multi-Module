package jsl.moum.auth.dto;


import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.moum.lifecycle.domain.Music;
import jsl.moum.rank.Rank;
import jsl.moum.record.domain.dto.RecordDto;
import jsl.moum.record.domain.entity.MoumMemberRecordEntity;
import jsl.moum.record.domain.entity.RecordEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MemberDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request{
        private int id;

        @NotEmpty @NotNull
        private String name;

        @NotEmpty @NotNull
        @Size(min = 3, max = 20)
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

        private String role;

        private String profileImageUrl;

        @NotEmpty @NotNull(message = "악기 숙련도를 입력해주세요")
        private String proficiency;

        @NotEmpty @NotNull(message = "사용 악기를 입력해주세요")
        private String instrument;

        @Nullable
        private List<RecordDto.Request> records;
        private String videoUrl;

        private List<MusicGenre> genres;
        private Boolean activeStatus;
        private Boolean banStatus;


        public MemberEntity toEntity(){
            return MemberEntity.builder()
                    .id(id)
                    .username(username)
                    .activeStatus(true)
                    .email(email)
                    .password(password)
                    .address(address)
                    .videoUrl(videoUrl)
                    .role(role)
                    .profileImageUrl(profileImageUrl)
                    .profileDescription(profileDescription)
                    .instrument(instrument)
                    .proficiency(proficiency)
                    .name(name)
//                    .records(records.stream().map(RecordDto.Request::toEntity).collect(Collectors.toList()))
//                    .records(records != null
//                            ? records.stream()
//                            .map(RecordDto.Request::toEntity)
//                            .collect(Collectors.toList())
//                            : Collections.emptyList())
                    .genres(genres)
                    .exp(0)
                    .tier(Rank.BRONZE)
                    .activeStatus(true)
                    .banStatus(false)
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
        private final List<MusicGenre> genres;
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

            this.genres = (member.getGenres() != null)
                    ? member.getGenres() : null;

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

    @Getter
    @AllArgsConstructor
    public static class Info{
        private final int id;
        private final String name;
        private final String username;
        private final String email;
        private final String role;
        private final String profileDescription;
        private final String profileImageUrl;
        private final Integer exp;
        private final List<MusicGenre> genres;
        private final Rank tier;
        private final String videoUrl;
        private final List<RecordDto.Response> memberRecords;
        private final List<RecordDto.Response> moumRecords;
        private final Boolean activeStatus;
        private final Boolean banStatus;


        public Info(MemberEntity member){
            this.id = member.getId();
            this.name = member.getName();
            this.username = member.getUsername();
            this.email = member.getEmail();
            this.role = member.getRole();
            this.profileDescription = member.getProfileDescription();
            this.profileImageUrl = member.getProfileImageUrl();
            this.videoUrl = member.getVideoUrl();
            this.exp = member.getExp();
            this.tier = member.getTier();
            this.activeStatus = member.getActiveStatus();
            this.banStatus = member.getBanStatus();
//            this.memberRecords = member.getRecords().stream()
//                    .map(RecordDto.Response::new)
//                    .collect(Collectors.toList());
//            this.moumRecords = member.getMoumMemberRecords().stream()
//                    .map(MoumMemberRecordEntity::getRecord)
//                    .map(RecordDto.Response::new)
//                    .collect(Collectors.toList());

            this.genres = (member.getGenres() != null)
                    ? member.getGenres() : null;

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

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    public static class RejoinRequest{
        @NotEmpty @NotNull private String username;
        @NotEmpty @NotNull private String email;
    }

}
