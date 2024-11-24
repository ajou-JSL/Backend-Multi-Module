package jsl.moum.community.pamphlet.dto;

import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import lombok.*;

import java.util.Date;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PamphletDto {

    private int id;
    private String teamName;
    private String moumName;
    private String performanceName;
    private String performanceDescription;
    private String performanceLocation;
    private Date performanceStartDate;
    private Date performanceEndDate;
    private Integer performancePrice;
    private String performanceImageUrl;
    private MusicGenre genre;

    public PamphletDto(PerformArticleEntity performArticle) {
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
    }

}
