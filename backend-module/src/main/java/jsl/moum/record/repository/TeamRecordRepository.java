package jsl.moum.record.repository;

import jsl.moum.record.domain.MemberRecordEntity;
import jsl.moum.record.domain.TeamRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRecordRepository extends JpaRepository<TeamRecordEntity, Integer> {
}
