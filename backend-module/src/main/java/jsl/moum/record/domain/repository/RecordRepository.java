package jsl.moum.record.domain.repository;

import jsl.moum.record.domain.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RecordRepository extends JpaRepository<RecordEntity, Integer> {
}
