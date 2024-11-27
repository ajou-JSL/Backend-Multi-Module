package jsl.moum.moum.lifecycle.domain.entity;

import jakarta.persistence.*;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
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
