package jsl.moum.community.perform.dto;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.dto.MemberDto;
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

public class PerformArticleDto {

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
        private int teamId;
        private int moumId;
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
        private final int teamId;
        private final String moumName;
        private final int moumId;
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
        private final Integer likesCount;
        private final Integer viewCount;

        public Response(PerformArticleEntity performArticle){
            this.id = performArticle.getId();
            this.teamName = performArticle.getTeam().getTeamName();
            this.teamId = performArticle.getTeam().getId();
            this.moumName = performArticle.getMoum().getLifecycleName();
            this.moumId = performArticle.getMoum().getId();
            this.performanceName = performArticle.getPerformanceName();
            this.performanceDescription = performArticle.getPerformanceDescription();
            this.performanceLocation = performArticle.getPerformanceLocation();
            this.performanceStartDate = performArticle.getPerformanceStartDate();
            this.performanceEndDate = performArticle.getPerformanceEndDate();
            this.performancePrice = performArticle.getPerformancePrice();
            this.performanceImageUrl = performArticle.getPerformanceImageUrl();
            this.genre = (performArticle.getGenre() != null) ? performArticle.getGenre() : null;
            this.viewCount = (performArticle.getViewCount() != null ? performArticle.getViewCount() : null);
            this.likesCount = (performArticle.getLikesCount() != null ? performArticle.getLikesCount() : null);

            // 멤버 id만 말고 객체 그대로 리턴할 경우에 이걸로
//            this.members = performArticle.getPerformMembers().stream()
//                    .map(PerformMember::getMember)
//                    .map(MemberDto.Response::new)
//                    .collect(Collectors.toList());

            // 멤버 id만 리턴할경우 이걸로
//            this.membersId = performArticle.getPerformMembers().stream()
//                    .map(PerformMember::getMember)
//                    .map(MemberEntity::getId)
//                    .collect(Collectors.toList());
//
            this.membersId = performArticle.getPerformMembers() != null
                    ? performArticle.getPerformMembers().stream()
                    .map(PerformMember::getMember)
                    .map(MemberEntity::getId)
                    .collect(Collectors.toList())
                    : new ArrayList<>();



        }
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Getter
    @Builder
    public static class SearchDto {
        private Boolean filterByCreatedAt;
        private Boolean filterByLikesCount;
        private Boolean filterByViewCount;
        private String keyword;
        private MusicGenre genre;

        private String location;
        private Date startDate;
        private Date endDate;
    }

}
