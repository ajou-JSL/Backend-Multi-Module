package study.moum.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import study.moum.record.domain.RecordEntity;

public interface RecordRepository extends JpaRepository<RecordEntity, Integer> {
}
