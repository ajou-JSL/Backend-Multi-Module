package jsl.moum.moum.lifecycle.dto;

import jsl.moum.auth.dto.MemberDto;
import jsl.moum.moum.lifecycle.domain.LifecycleEntity;
import jsl.moum.moum.lifecycle.domain.LifecycleTeamEntity;
import jsl.moum.moum.team.domain.TeamEntity;
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
        private String lifecycleName;
        private String lifecycleDescription;
        private String performLocation;
        private LocalDate startDate;
        private LocalDate endDate;
        private int price;
        private String imageUrl;

        public LifecycleEntity toEntity(){
            return LifecycleEntity.builder()
                    .lifecycleName(lifecycleName)
                    .lifecycleDescription(lifecycleDescription)
                    .performLocation(performLocation)
                    .startDate(startDate)
                    .endDate(endDate)
                    .price(price)
                    .imageUrl(imageUrl)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{
        private String lifecycleName;
        private String lifecycleDescription;
        private String performLocation;
        private LocalDate startDate;
        private LocalDate endDate;
        private int price;
        private String imageUrl;
        private List<MemberDto.Response> members = new ArrayList<>();

        public Response(LifecycleEntity lifecycle){
            this.lifecycleName = lifecycle.getLifecycleName();
            this.lifecycleDescription = lifecycle.getLifecycleDescription();
            this.performLocation = lifecycle.getPerformLocation();
            this.startDate = lifecycle.getStartDate();
            this.endDate = lifecycle.getEndDate();
            this.price = lifecycle.getPrice();
            this.imageUrl = lifecycle.getImageUrl();
            this.members = lifecycle.getTeams().stream()
                    .map(LifecycleTeamEntity::getTeam)
                    .flatMap(team -> team.getMembers().stream())
                    .map(TeamMemberEntity::getMember)
                    .map(MemberDto.Response::new)
                    .collect(Collectors.toList());
        }
    }
}
