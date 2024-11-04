package jsl.moum.moum.lifecycle.domain;

import jakarta.persistence.*;
import jsl.moum.auth.dto.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.XSlf4j;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity(name = "lifecycle")
public class LifecycleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "lifecycle_name")
    private String lifecycleName;

    @Column(name = "lifecycle_description")
    private String lifecycleDescription;

    @Column(name = "peform_location")
    private String performLocation;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "price")
    private int price;

    @Column(name = "image_url")
    private String imageUrl;

    @OneToMany(mappedBy = "lifecycle", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LifecycleTeamEntity> teams = new ArrayList<>();

    @Embedded
    private Process process;

    @PrePersist
    @PreUpdate
    public void updateProcessPercentage() {
        if (process != null) {
            process.updateProcessPercentage();
        }
    }

    public int getPercentage(){
        return process.updateProcessPercentage();
    }

}
