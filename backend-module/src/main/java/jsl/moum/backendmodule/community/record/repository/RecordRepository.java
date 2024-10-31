package jsl.moum.backendmodule.community.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import jsl.moum.backendmodule.community.record.domain.RecordEntity;

public interface RecordRepository extends JpaRepository<RecordEntity, Integer> {
}
