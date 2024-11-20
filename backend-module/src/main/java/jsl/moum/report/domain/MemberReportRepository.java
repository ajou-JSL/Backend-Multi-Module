package jsl.moum.report.domain;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberReportRepository extends JpaRepository<MemberReport, Integer> {

    @Query("SELECT COUNT(mr) > 0 FROM MemberReport mr WHERE mr.member.id = :memberId AND mr.reporter.id = :reporterId")
    boolean existsByReporterAndMember(@Param("reporterId") int reporterId, @Param("memberId") int memberId);

}
