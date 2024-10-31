package study.moum.record.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import study.moum.auth.domain.entity.MemberEntity;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "member_record")
public class MemberRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "fk_member_id")
    private MemberEntity member;

    @ManyToOne
    @JoinColumn(name = "fk_record_id")
    private RecordEntity record;

}
