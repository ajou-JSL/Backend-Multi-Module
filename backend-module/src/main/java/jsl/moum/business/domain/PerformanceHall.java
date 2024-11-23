package jsl.moum.business.domain;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "performance_hall")
public class PerformanceHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column
    private String name;

    @Column
    private int price;

    @Column
    private int size;

    @Column
    private int capacity;

    @Column
    private String address;

    @Column
    private String owner;

    @Column
    private String phone;

    @Column
    private String email;

    @Column(name = "map_url")
    private String mapUrl;

    @ElementCollection // 다중 값 저장
    @CollectionTable(name = "performance_hall_images", joinColumns = @JoinColumn(name = "performance_hall_id"))
    @Column(name = "image_url")
    private List<String> imageUrls = new ArrayList<>();

    @Column
    private int type;

    @Column
    private int stand;

    @Column(name = "has_piano")
    private boolean hasPiano;

    @Column(name = "has_amp")
    private boolean hasAmp;

    @Column(name = "has_speaker")
    private boolean hasSpeaker;

    @Column(name = "has_mic")
    private boolean hasMic;

    @Column(name = "has_drums")
    private boolean hasDrums;

    @Column
    private String details;

    @Column(columnDefinition = "DOUBLE")
    private Double latitude;

    @Column(columnDefinition = "DOUBLE")
    private Double longitude;
}
