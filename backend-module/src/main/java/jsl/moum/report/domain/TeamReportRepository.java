package jsl.moum.report.domain;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamReportRepository extends JpaRepository<TeamReport, Integer> {

    // Method to check if a TeamReport exists for a given reporterId and teamId
    @Query("SELECT COUNT(tr) > 0 FROM TeamReport tr WHERE tr.reporter.id = :memberId AND tr.team.id = :teamId")
    boolean existsByReporterAndTeam(@Param("memberId") int memberId, @Param("teamId") int teamId);

    @Query("SELECT tr FROM TeamReport tr WHERE tr.reporter.id = :reporterId")
    Page<TeamReport> findAllByReporterId(@Param("reporterId") int reporterId, Pageable pageable);

    @Query("SELECT tr FROM TeamReport tr WHERE tr.team.id = :teamId")
    Page<TeamReport> findAllByTeamId(@Param("teamId") int teamId, Pageable pageable);
}
