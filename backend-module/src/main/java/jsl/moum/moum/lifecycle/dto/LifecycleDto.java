package jsl.moum.moum.lifecycle.dto;

import jsl.moum.auth.dto.MemberDto;
import jsl.moum.moum.lifecycle.domain.LifecycleEntity;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import jsl.moum.moum.team.dto.TeamDto;
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
        private int leaderId;
        private String leaderName;
        private int teamId; // 어느 팀의 라이프사이클인지 알아야하니까

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
                    .leaderName(leaderName)
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
        private List<MemberDto.Response> members = new ArrayList<>();

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
//            this.members = lifecycle.getTeam().getMembers().stream()
//                    .map(TeamMemberEntity::getMember)
//                    .map(MemberDto.Response::new)
//                    .collect(Collectors.toList());
            // getMember랑 getTeam이 null이 아닐때만 하도록해서 테스트코드 넘길수있게끔
            this.members = (lifecycle.getTeam() != null && lifecycle.getTeam().getMembers() != null)
                    ? lifecycle.getTeam().getMembers().stream()
                    .map(TeamMemberEntity::getMember)  // TeamMemberEntity에서 MemberEntity로 변환
                    .map(MemberDto.Response::new)     // MemberEntity를 MemberDto.Response로 변환
                    .collect(Collectors.toList())
                    : new ArrayList<>();  // 만약 team이나 members가 null이라면 빈 리스트로 초기화


        }
    }
}
