package jsl.moum.record.domain.entity;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import lombok.*;

@Builder
@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
@Entity(name = "moum_member_record")
public class MoumMemberRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "fk_member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "fk_record_id")
    private RecordEntity record;

    @ManyToOne
    @JoinColumn(name = "fk_lifecycle_id")
    private LifecycleEntity lifecycle;
}
