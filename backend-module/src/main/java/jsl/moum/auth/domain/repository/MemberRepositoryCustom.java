package jsl.moum.auth.domain.repository;

import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.auth.dto.MemberSortDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

import static jsl.moum.auth.domain.entity.QMemberEntity.memberEntity;
import static jsl.moum.record.domain.entity.QMoumMemberRecordEntity.moumMemberRecordEntity;
import static jsl.moum.record.domain.entity.QRecordEntity.recordEntity;

@Repository
@RequiredArgsConstructor
public class MemberRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 랭킹(exp) 순 멤버 리스트 조회
     */
    public List<MemberSortDto.ExpResponse> getMembersSortByExp(int page, int size) {
        List<Tuple> results = jpaQueryFactory
                .select(
                        memberEntity.id,
                        memberEntity.name,
                        memberEntity.username,
                        memberEntity.exp,
                        memberEntity.tier
                )
                .from(memberEntity)
                .orderBy(memberEntity.exp.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();

        return results.stream()
                .map(tuple -> {
                    Integer memberId = tuple.get(memberEntity.id);
                    String memberName = tuple.get(memberEntity.name);
                    String memberUsername = tuple.get(memberEntity.username);
                    int exp = tuple.get(memberEntity.exp);
                    String tier = tuple.get(memberEntity.tier).getTier();

                    return new MemberSortDto.ExpResponse(
                            memberId,
                            memberName,
                            memberUsername,
                            exp,
                            tier
                    );
                })
                .collect(Collectors.toList());
    }


    /**
     * 이력 개수 순 멤버 리스트 조회
     */
    public List<MemberSortDto.RecordsCountResponse> getMembersSortByRecordsCount(int page, int size) {
        List<Tuple> results = jpaQueryFactory
                .select(
                        memberEntity.id,
                        memberEntity.name,
                        memberEntity.username,
                        recordEntity.count().add(moumMemberRecordEntity.count())
                )
                .from(memberEntity)
                .leftJoin(memberEntity.records, recordEntity)
                .leftJoin(memberEntity.moumMemberRecords, moumMemberRecordEntity)
                .groupBy(memberEntity.id)
                .orderBy(
                        recordEntity.count().add(moumMemberRecordEntity.count()).desc()
                )
                .limit(size)
                .offset((long)page * size)
                .fetch();

        return results.stream()
                .map(tuple -> {
                    Integer memberId = tuple.get((memberEntity.id));
                    String memberName = tuple.get(memberEntity.name);
                    String memberUsername = tuple.get(memberEntity.username);
                    Long totalRecordCount = tuple.get(3, Long.class);

                    return new MemberSortDto.RecordsCountResponse(
                            memberId,
                            memberName,
                            memberUsername,
                            totalRecordCount.intValue()
                    );
                })
                .collect(Collectors.toList());
    }

}
