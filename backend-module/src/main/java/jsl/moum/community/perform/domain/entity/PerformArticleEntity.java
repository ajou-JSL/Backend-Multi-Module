package jsl.moum.community.perform.domain.entity;

import jakarta.persistence.*;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.moum.lifecycle.domain.LifecycleEntity;
import jsl.moum.moum.team.domain.TeamEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Table(name = "perform_article")
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
    private int performancePrice;

    @Column(name = "performance_image_url")
    private String performanceImageUrl;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @ElementCollection(targetClass = MusicGenre.class)
    @CollectionTable(name = "perform_genre", joinColumns = @JoinColumn(name = "perform_id"))
    @Column(name = "genre", nullable = false)
    @Enumerated(EnumType.STRING)
    private List<MusicGenre> genres = new ArrayList<>();

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


}
