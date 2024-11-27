package jsl.moum.moum.settlement.domain.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Entity
@Table(name = "settlement")
public class SettlementEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "settlement_name")
    private String settlementName;

    @Column(name = "fee")
    private int fee;

    @Column(name = "fk_moum_id")
    private Integer moumId;
}
