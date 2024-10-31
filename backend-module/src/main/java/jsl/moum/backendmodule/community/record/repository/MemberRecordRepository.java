package jsl.moum.backendmodule.community.record.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import jsl.moum.backendmodule.community.record.domain.MemberRecordEntity;

public interface MemberRecordRepository extends JpaRepository<MemberRecordEntity, Integer> {
}
