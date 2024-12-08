package jsl.moum.moum.team.dto;

import io.micrometer.common.lang.Nullable;
import jakarta.validation.constraints.NotNull;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import jsl.moum.rank.Rank;
import jsl.moum.record.domain.dto.RecordDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class TeamDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request{
        private String teamName;
        private String description;

        @NotNull
        private int leaderId;
        private List<TeamMemberEntity> members;
        private String fileUrl;
        private MusicGenre genre;
        private String location;
        @Nullable
        private List<RecordDto.Request> records;
        private String videoUrl;

        public TeamEntity toEntity(){
            return TeamEntity.builder()
                    .members(members)
                    .teamName(teamName)
                    .genre(genre)
                    .location(location)
                    .description(description)
                    .leaderId(leaderId)
                    .fileUrl(fileUrl)
                    .videoUrl(videoUrl)
                    .records(records.stream().map(RecordDto.Request::toEntity).collect(Collectors.toList()))
                    .exp(0)
                    .tier(Rank.BRONZE)
                    .build();
        }

    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private final int teamId;
        private final int leaderId;
        private final String teamName;
        private final String description;
        private final MusicGenre genre;
        private final String location;
        private final LocalDateTime createdAt;
        private final String fileUrl;
        private final String videoUrl;
        private final Integer exp;
        private final Rank tier;
        private final List<MemberDto.Response> members;
        private final List<RecordDto.Response> records;

        public Response(TeamEntity teamEntity){
            this.teamId = teamEntity.getId();
            this.leaderId = teamEntity.getLeaderId();
            this.teamName = teamEntity.getTeamName();
            this.description = teamEntity.getDescription();
            this.genre = teamEntity.getGenre();
            this.location = teamEntity.getLocation();
            this.createdAt = teamEntity.getCreatedAt();
            this.fileUrl = teamEntity.getFileUrl();
            this.videoUrl = teamEntity.getVideoUrl();
            this.exp = teamEntity.getExp();
            this.tier = teamEntity.getTier();

            this.members = teamEntity.getMembers().stream()
                    .map(TeamMemberEntity::getMember) // TeamMemberEntity에서 MemberEntity 가져오기
                    .map(MemberDto.Response::new) // MemberEntity를 MemberDto.Response로 변환
                    .collect(Collectors.toList());

            this.records = (teamEntity.getRecords() != null)
                    ? teamEntity.getRecords().stream()
                    .map(RecordDto.Response::new)
                    .collect(Collectors.toList()) : null;
        }
    }


    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class UpdateRequest{
        private String teamName;
        private String description;
        private String location;
        private String fileUrl;
        private String videoUrl;
        @Nullable
        private List<RecordDto.Request> records;
        private MusicGenre genre;


        public TeamEntity toEntity(){
            return TeamEntity.builder()
                    .teamName(teamName)
                    .description(description)
                    .genre(genre)
                    .location(location)
                    .fileUrl(fileUrl)
                    .videoUrl(videoUrl)
                    .records(records.stream().map(RecordDto.Request::toEntity).collect(Collectors.toList()))
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class UpdateResponse{
        private final int teamId;
        private final int leaderId;
        private final String teamName;
        private final String description;
        private final MusicGenre genre;
        private final String location;
        private final LocalDateTime createdAt;
        private final String fileUrl;
        private final String videoUrl;
        private final List<RecordDto.Response> records;

        public UpdateResponse(TeamEntity teamEntity){
            this.teamId = teamEntity.getId();
            this.leaderId = teamEntity.getLeaderId();
            this.teamName = teamEntity.getTeamName();
            this.description = teamEntity.getDescription();
            this.genre = teamEntity.getGenre();
            this.location = teamEntity.getLocation();
            this.createdAt = teamEntity.getCreatedAt();
            this.fileUrl = teamEntity.getFileUrl();
            this.videoUrl = teamEntity.getVideoUrl();
            this.records = teamEntity.getRecords().stream()
                    .map(RecordDto.Response::new)
                    .collect(Collectors.toList());;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Builder
    public static class SearchDto {
        private Boolean filterByExp;
        private Boolean filterByMembersCount;
        private String keyword;
        private MusicGenre genre;
        private String location;
    }
}
