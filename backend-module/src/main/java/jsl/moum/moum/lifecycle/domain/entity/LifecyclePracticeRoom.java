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
@Table(name = "lifecycle_practice_room")
public class LifecyclePracticeRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "moum_id")
    private LifecycleEntity moum;

    @Column(name = "room_id")
    private Integer roomId;

    @Column(name = "practice_room")
    private String practiceRoom;
}
