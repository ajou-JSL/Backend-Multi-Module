package jsl.moum.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import jsl.moum.record.domain.MemberRecordEntity;

public interface MemberRecordRepository extends JpaRepository<MemberRecordEntity, Integer> {
}
