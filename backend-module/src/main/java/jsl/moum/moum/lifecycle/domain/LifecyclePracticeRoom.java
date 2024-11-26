package jsl.moum.moum.lifecycle.domain;

import jakarta.persistence.*;
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
