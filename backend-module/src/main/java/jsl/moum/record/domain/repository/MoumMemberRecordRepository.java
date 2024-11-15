package jsl.moum.record.domain.repository;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.record.domain.entity.MoumMemberRecordEntity;
import jsl.moum.record.domain.entity.RecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MoumMemberRecordRepository extends JpaRepository<MoumMemberRecordEntity,Integer> {
}
