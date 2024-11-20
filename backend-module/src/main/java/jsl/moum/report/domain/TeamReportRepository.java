package jsl.moum.report.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeamReportRepository extends JpaRepository<TeamReport, Integer> {
}
