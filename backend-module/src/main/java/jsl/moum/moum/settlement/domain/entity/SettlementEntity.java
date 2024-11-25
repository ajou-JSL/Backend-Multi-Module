package jsl.moum.moum.settlement.domain.entity;

import jakarta.persistence.*;
import jsl.moum.moum.lifecycle.domain.LifecycleEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Table(name = "settlement")
public class SettlementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "settlement_name")
    private String settlementName;

    @Column(name = "fee")
    private String fee;

    @ManyToOne
    @JoinColumn(name = "fk_moum_id")
    private LifecycleEntity moumId;
}
