package study.moum.moum.team.dto;

import lombok.*;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.dto.MemberDto;
import study.moum.community.comment.dto.CommentDto;
import study.moum.moum.team.domain.TeamEntity;
import study.moum.moum.team.domain.TeamMemberEntity;

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
        private String teamname;
        private String description;
        private int leaderId;
        private List<TeamMemberEntity> members;

        public TeamEntity toEntity(){
            return TeamEntity.builder()
                    .members(members)
                    .teamname(teamname)
                    .description(description)
                    .leaderId(leaderId)
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
        private List<MemberDto.Response> members = new ArrayList<>();
        private LocalDateTime createdAt;

        public Response(TeamEntity teamEntity){
            this.teamId = teamEntity.getId();
            this.leaderId = teamEntity.getLeaderId();
            this.teamName = teamEntity.getTeamname();
            this.description = teamEntity.getDescription();
            this.members = teamEntity.getMembers().stream()
                    .map(TeamMemberEntity::getMember) // TeamMemberEntity에서 MemberEntity 가져오기
                    .map(MemberDto.Response::new) // MemberEntity를 MemberDto.Response로 변환
                    .collect(Collectors.toList());
            this.createdAt = teamEntity.getCreatedAt();
        }
    }
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class UpdateRequest{
        private String teamname;
        private String description;

        public TeamEntity toEntity(){
            return TeamEntity.builder()
                    .teamname(teamname)
                    .description(description)
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
        private LocalDateTime createdAt;

        public UpdateResponse(TeamEntity teamEntity){
            this.teamId = teamEntity.getId();
            this.leaderId = teamEntity.getLeaderId();
            this.teamName = teamEntity.getTeamname();
            this.description = teamEntity.getDescription();
            this.createdAt = teamEntity.getCreatedAt();
        }
    }
}
