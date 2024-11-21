package jsl.moum.community.perform.dto;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.community.perform.domain.entity.PerformMember;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class PerformArticleUpdateDto {

    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    public static class Request{
        private String performanceName;
        private String performanceDescription;
        private String performanceLocation;
        private Date performanceStartDate;
        private Date performanceEndDate;
        private Integer performancePrice;
        private String performanceImageUrl;
        private List<Integer> membersId;
        private MusicGenre genre;

        public PerformArticleEntity toEntity(){
            return PerformArticleEntity.builder()
                    .performanceName(performanceName)
                    .performanceDescription(performanceDescription)
                    .performanceLocation(performanceLocation)
                    .performanceStartDate(performanceStartDate)
                    .performanceEndDate(performanceEndDate)
                    .performancePrice(performancePrice)
                    .performanceImageUrl(performanceImageUrl)
                    .genre(genre)
                    .build();
        }
    }

    @Getter
    @AllArgsConstructor
    public static class Response{

        private final int id;
        private final String teamName;
        private final String moumName;
        private final String performanceName;
        private final String performanceDescription;
        private final String performanceLocation;
        private final Date performanceStartDate;
        private final Date performanceEndDate;
        private final Integer performancePrice;
        private final String performanceImageUrl;
        //private final List<MemberDto.Response> members;
        private final List<Integer> membersId;
        private final MusicGenre genre;

        public Response(PerformArticleEntity performArticle){
            this.id = performArticle.getId();
            this.teamName = performArticle.getTeam().getTeamName();
            this.moumName = performArticle.getMoum().getLifecycleName();
            this.performanceName = performArticle.getPerformanceName();
            this.performanceDescription = performArticle.getPerformanceDescription();
            this.performanceLocation = performArticle.getPerformanceLocation();
            this.performanceStartDate = performArticle.getPerformanceStartDate();
            this.performanceEndDate = performArticle.getPerformanceEndDate();
            this.performancePrice = performArticle.getPerformancePrice();
            this.performanceImageUrl = performArticle.getPerformanceImageUrl();
            this.genre = (performArticle.getGenre() != null) ? performArticle.getGenre() : null;
//
            this.membersId = performArticle.getPerformMembers() != null
                    ? performArticle.getPerformMembers().stream()
                    .map(PerformMember::getMember)
                    .map(MemberEntity::getId)
                    .collect(Collectors.toList())
                    : new ArrayList<>();



        }
    }
}
