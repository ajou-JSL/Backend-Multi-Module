package jsl.moum.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import jsl.moum.record.domain.RecordEntity;

public interface RecordRepository extends JpaRepository<RecordEntity, Integer> {
}
