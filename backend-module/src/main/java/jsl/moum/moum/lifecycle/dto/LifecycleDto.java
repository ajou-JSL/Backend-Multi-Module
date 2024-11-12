package jsl.moum.moum.lifecycle.dto;

import jakarta.validation.constraints.NotNull;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.moum.lifecycle.domain.LifecycleEntity;
import jsl.moum.moum.lifecycle.domain.Process;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import jsl.moum.moum.team.dto.TeamDto;
import jsl.moum.record.domain.dto.RecordDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class LifecycleDto {

    @Builder
    @Getter
    @AllArgsConstructor
    public static class Request{
        private String moumName;
        private String moumDescription;
        private String performLocation;
        private LocalDate startDate;
        private LocalDate endDate;
        private int price;
        private String imageUrl;

        @NotNull
        private int leaderId;

        @NotNull
        private int teamId; // 어느 팀의 라이프사이클인지 알아야하니까

        private List<Integer> members;
        private List<RecordDto.Request> records;
        private Process process;


        public LifecycleEntity toEntity(){
            return LifecycleEntity.builder()
                    .lifecycleName(moumName)
                    .lifecycleDescription(moumDescription)
                    .performLocation(performLocation)
                    .startDate(startDate)
                    .endDate(endDate)
                    .price(price)
                    .imageUrl(imageUrl)
                    .leaderId(leaderId)
                    .records(records.stream().map(RecordDto.Request::toEntity).collect(Collectors.toList()))
                    .process(process)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private int moumId;
        private String moumName;
        private String moumDescription;
        private String performLocation;
        private LocalDate startDate;
        private LocalDate endDate;
        private int price;
        private String imageUrl;
        private int leaderId;
        private String leaderName;
        private int teamId;
        private Process process;
        private List<MemberDto.Response> members = new ArrayList<>();
        private List<RecordDto.Response> records = new ArrayList<>();

        public Response(LifecycleEntity lifecycle){
            this.moumId = lifecycle.getId();
            this.moumName = lifecycle.getLifecycleName();
            this.moumDescription = lifecycle.getLifecycleDescription();
            this.performLocation = lifecycle.getPerformLocation();
            this.startDate = lifecycle.getStartDate();
            this.endDate = lifecycle.getEndDate();
            this.price = lifecycle.getPrice();
            this.imageUrl = lifecycle.getImageUrl();
            this.leaderId = lifecycle.getLeaderId();
            this.leaderName = lifecycle.getLeaderName();
            this.teamId = lifecycle.getTeam().getId();
            this.process = lifecycle.getProcess();

            this.members = lifecycle.getTeam().getMembers().stream()
                    .map(TeamMemberEntity::getMember)
                    .map(MemberDto.Response::new)
                    .collect(Collectors.toList());

            this.records = lifecycle.getRecords().stream()
                    .map(RecordDto.Response::new)
                    .collect(Collectors.toList());;

        }
    }
}
