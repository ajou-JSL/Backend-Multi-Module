package jsl.moum.community.perform.domain.entity;

import jakarta.persistence.*;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.community.perform.dto.PerformArticleUpdateDto;
import jsl.moum.moum.lifecycle.domain.LifecycleEntity;
import jsl.moum.moum.team.domain.TeamEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Slf4j
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "perform_article",
        indexes = {
                @Index(name = "idx_perform_created_at", columnList = "created_at DESC"),
        })
public class PerformArticleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "performance_name")
    private String performanceName;

    @Column(name = "performance_description")
    private String performanceDescription;

    @Column(name = "performance_location")
    private String performanceLocation;

    @Column(name = "performance_start_date")
    private Date performanceStartDate;

    @Column(name = "performance_end_date")
    private Date performanceEndDate;

    @Column(name = "performance_price")
    private Integer performancePrice;

    @Column(name = "performance_image_url")
    private String performanceImageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "genre", nullable = false)
    private MusicGenre genre;

//    @Column(name = "likes_count")
//    private int likesCount;
//
//    @Column(name = "view_count")
//    private int viewCount;

    // 멤버가 참여해있는 공연들
    @OneToMany(mappedBy = "performanceArticle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PerformMember> performMembers = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "fk_team_id")
    private TeamEntity team;

    @ManyToOne
    @JoinColumn(name = "fk_moum_id")
    private LifecycleEntity moum;

    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    public void updatePerformArticle(PerformArticleUpdateDto.Request updateDto) {
        if (updateDto.getPerformanceName() != null) {
            this.performanceName = updateDto.getPerformanceName();
        }
        if (updateDto.getPerformanceDescription() != null) {
            this.performanceDescription = updateDto.getPerformanceDescription();
        }
        if (updateDto.getPerformanceLocation() != null) {
            this.performanceLocation = updateDto.getPerformanceLocation();
        }
        if (updateDto.getPerformanceStartDate() != null) {
            this.performanceStartDate = updateDto.getPerformanceStartDate();
        }
        if (updateDto.getPerformanceEndDate() != null) {
            this.performanceEndDate = updateDto.getPerformanceEndDate();
        }
        if (updateDto.getPerformancePrice() != null) {
            this.performancePrice = updateDto.getPerformancePrice();
        }
        if (updateDto.getPerformanceImageUrl() != null) {
            this.performanceImageUrl = updateDto.getPerformanceImageUrl();
        }
        if (updateDto.getGenre() != null) {
            this.genre = updateDto.getGenre();
        }
    }
//
//    public void updateLikesCount(int count){
//        log.info("#1 : updateLikesCount() method called, likesCount: {}", this.likesCount);
//        this.likesCount += count;
//        log.info("#2 : updateLikesCount() method called, likesCount: {}", this.likesCount);
//    }

//    public void updateViewCount(int count){
//        log.info("#1 : updateViewCount() method called, viewCount: {}", this.viewCount);
//        this.viewCount += count;
//        log.info("#2 : updateViewCount() method called, viewCount: {}", this.viewCount);
//
//    }
}
