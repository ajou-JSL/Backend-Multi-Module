package study.moum.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.moum.record.domain.MemberRecordEntity;

public interface MemberRecordRepository extends JpaRepository<MemberRecordEntity, Integer> {
}
