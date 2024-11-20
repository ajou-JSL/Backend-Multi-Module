package jsl.moum.report.domain;

import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamReportRepository extends JpaRepository<TeamReport, Integer> {

    // Method to check if a TeamReport exists for a given reporterId and teamId
    @Query("SELECT COUNT(tr) > 0 FROM TeamReport tr WHERE tr.reporter.id = :memberId AND tr.team.id = :teamId")
    boolean existsByReporterAndTeam(@Param("memberId") int memberId, @Param("teamId") int teamId);

}
