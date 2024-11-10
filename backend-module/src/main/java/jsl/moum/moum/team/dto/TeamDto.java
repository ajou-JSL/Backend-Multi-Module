package jsl.moum.moum.team.dto;

import jakarta.validation.constraints.NotNull;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberEntity;
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

        private String genre;
        private String location;
        // private 이력


        public TeamEntity toEntity(){
            return TeamEntity.builder()
                    .members(members)
                    .teamName(teamName)
                    .genre(genre)
                    .location(location)
                    .description(description)
                    .leaderId(leaderId)
                    .fileUrl(fileUrl)
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
        private final String genre;
        private final String location;
        private LocalDateTime createdAt;
        private String fileUrl;
        private List<MemberDto.Response> members = new ArrayList<>();

        public Response(TeamEntity teamEntity){
            this.teamId = teamEntity.getId();
            this.leaderId = teamEntity.getLeaderId();
            this.teamName = teamEntity.getTeamName();
            this.description = teamEntity.getDescription();
            this.genre = teamEntity.getGenre();
            this.location = teamEntity.getLocation();
            this.createdAt = teamEntity.getCreatedAt();
            this.fileUrl = teamEntity.getFileUrl();

            this.members = teamEntity.getMembers().stream()
                    .map(TeamMemberEntity::getMember) // TeamMemberEntity에서 MemberEntity 가져오기
                    .map(MemberDto.Response::new) // MemberEntity를 MemberDto.Response로 변환
                    .collect(Collectors.toList());
        }

    }


    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class UpdateRequest{
        private String teamName;
        private String description;
        private String genre;
        private String location;
        private String fileUrl;

        public TeamEntity toEntity(){
            return TeamEntity.builder()
                    .teamName(teamName)
                    .description(description)
                    .genre(genre)
                    .location(location)
                    .fileUrl(fileUrl)
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
        private final String genre;
        private final String locaion;
        private LocalDateTime createdAt;
        private String fileUrl;

        public UpdateResponse(TeamEntity teamEntity){
            this.teamId = teamEntity.getId();
            this.leaderId = teamEntity.getLeaderId();
            this.teamName = teamEntity.getTeamName();
            this.description = teamEntity.getDescription();
            this.genre = teamEntity.getGenre();
            this.locaion = teamEntity.getLocation();
            this.createdAt = teamEntity.getCreatedAt();
            this.fileUrl = teamEntity.getFileUrl();
        }
    }
}
