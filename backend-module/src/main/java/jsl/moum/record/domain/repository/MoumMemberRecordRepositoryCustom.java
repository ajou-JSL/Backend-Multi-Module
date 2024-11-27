package jsl.moum.record.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import static jsl.moum.record.domain.entity.QMoumMemberRecordEntity.moumMemberRecordEntity;

@Repository
@RequiredArgsConstructor
public class MoumMemberRecordRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     해당 모음에 속한 멤버들의 모든 이력을 삭제
       DELETE FROM moum_member_record
       WHERE lifecycle_id = :lifecycleId
       AND member_id = :memberId;
     */
    public void deleteMoumMemberRecordByLifecycleAndMember(int lifecycleId, int memberId) {
        long deletedCount = jpaQueryFactory
                .delete(moumMemberRecordEntity)
                .where(
                        moumMemberRecordEntity.lifecycle.id.eq(lifecycleId)
                                .and(moumMemberRecordEntity.member.id.eq(memberId))
                )
                .execute();

        if (deletedCount == 0) {
            throw new CustomException(ErrorCode.MEMBER_NOT_EXIST);
        }
    }
}