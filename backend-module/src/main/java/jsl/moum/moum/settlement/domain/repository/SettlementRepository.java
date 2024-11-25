package jsl.moum.moum.settlement.domain.repository;

import jsl.moum.moum.settlement.domain.entity.SettlementEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SettlementRepository extends JpaRepository<SettlementEntity, Integer> {
}
