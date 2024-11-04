package jsl.moum.moum.lifecycle.dto;

import jsl.moum.auth.dto.MemberDto;
import jsl.moum.moum.lifecycle.domain.LifecycleEntity;
import jsl.moum.moum.lifecycle.domain.LifecycleTeamEntity;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

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

        public LifecycleEntity toEntity(){
            return LifecycleEntity.builder()
                    .lifecycleName(moumName)
                    .lifecycleDescription(moumDescription)
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
        private int moumId;
        private String moumName;
        private String moumDescription;
        private String performLocation;
        private LocalDate startDate;
        private LocalDate endDate;
        private int price;
        private String imageUrl;
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
            this.members = lifecycle.getTeams() == null
                    ? null
                    : lifecycle.getTeams().stream()
                    .map(LifecycleTeamEntity::getTeam)
                    .filter(Objects::nonNull) // team이 null이 아닌 경우만 처리
                    .flatMap(team -> team.getMembers() == null ? Stream.empty() : team.getMembers().stream())
                    .map(TeamMemberEntity::getMember)
                    .filter(Objects::nonNull) // member가 null이 아닌 경우만 처리
                    .map(MemberDto.Response::new)
                    .collect(Collectors.toList());

        }
    }
}
