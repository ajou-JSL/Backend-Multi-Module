package jsl.moum.report.domain;

import jakarta.persistence.*;
import jsl.moum.auth.domain.entity.MemberEntity;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "member_report")
public class MemberReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // Foreign key reference to the member
    @ManyToOne
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    private MemberEntity member;

    // Storing the member's username directly
    @Column(name = "member_username", nullable = false)
    private String memberUsername;

    // Foreign key reference to the reporter
    @ManyToOne
    @JoinColumn(name = "reporter_id", referencedColumnName = "id", nullable = false)
    private MemberEntity reporter;

    // Storing the reporter's username directly
    @Column(name = "reporter_username", nullable = false)
    private String reporterUsername;

    @Column(name = "type")
    private String type;

    @Column(name = "details")
    private String details;
}
