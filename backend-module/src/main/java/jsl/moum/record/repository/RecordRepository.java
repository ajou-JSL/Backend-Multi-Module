package jsl.moum.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import jsl.moum.record.domain.RecordEntity;

import java.util.List;

public interface RecordRepository extends JpaRepository<RecordEntity, Integer> {
    List<RecordEntity> findByTeamsId(int teamId);
}
