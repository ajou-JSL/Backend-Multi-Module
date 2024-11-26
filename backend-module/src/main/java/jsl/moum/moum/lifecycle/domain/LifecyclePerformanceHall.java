package jsl.moum.moum.lifecycle.domain;

import jakarta.persistence.*;
import lombok.*;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Getter
@Setter
@Table(name = "lifecycle_performance_hall")
public class LifecyclePerformanceHall {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "moum_id")
    private LifecycleEntity moum;

    @Column(name = "hall_id")
    private Integer hallId;

    @Column(name = "performance_hall")
    private String performanceHall;
}
