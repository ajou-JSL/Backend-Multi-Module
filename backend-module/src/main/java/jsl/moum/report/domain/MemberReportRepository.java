package jsl.moum.report.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MemberReportRepository extends JpaRepository<MemberReport, Integer> {

    @Query("SELECT COUNT(mr) > 0 FROM MemberReport mr WHERE mr.member.id = :memberId AND mr.reporter.id = :reporterId")
    boolean existsByReporterAndMember(@Param("reporterId") int reporterId, @Param("memberId") int memberId);

    @Query("SELECT mr FROM MemberReport mr WHERE mr.reporter.id = :reporterId")
    Page<MemberReport> findAllByReporterId(@Param("reporterId") int reporterId, Pageable pageable);

    @Query("SELECT mr FROM MemberReport mr WHERE mr.member.id = :memberId")
    Page<MemberReport> findAllByMemberId(@Param("memberId") int memberId, Pageable pageable);
}
