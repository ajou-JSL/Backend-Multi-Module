package jsl.moum.moum.team.dto;

import jsl.moum.auth.dto.MemberDto;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import jsl.moum.record.dto.RecordDto;
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
        private String teamname;
        private String description;
        private int leaderId;
        private List<TeamMemberEntity> members;
        private String fileUrl;

        // update용 dto필드. update는 entity 내부 메소드
        private List<RecordDto.Request> records;

        public TeamEntity toEntity(){
            return TeamEntity.builder()
                    .members(members)
                    .teamname(teamname)
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
        private List<MemberDto.Response> members = new ArrayList<>();
        private LocalDateTime createdAt;
        private String fileUrl;
        private List<RecordDto.Response> records = new ArrayList<>();

        public Response(TeamEntity teamEntity){
            this.teamId = teamEntity.getId();
            this.leaderId = teamEntity.getLeaderId();
            this.teamName = teamEntity.getTeamname();
            this.description = teamEntity.getDescription();
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
        private String teamname;
        private String description;
        private String fileUrl;
        private List<RecordDto.Request> records;

        public TeamEntity toEntity(){
            return TeamEntity.builder()
                    .teamname(teamname)
                    .description(description)
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
        private LocalDateTime createdAt;
        private String fileUrl;
        private List<RecordDto.Response> records = new ArrayList<>();

        public UpdateResponse(TeamEntity teamEntity){
            this.teamId = teamEntity.getId();
            this.leaderId = teamEntity.getLeaderId();
            this.teamName = teamEntity.getTeamname();
            this.description = teamEntity.getDescription();
            this.createdAt = teamEntity.getCreatedAt();
            this.fileUrl = teamEntity.getFileUrl();
        }
    }
}
