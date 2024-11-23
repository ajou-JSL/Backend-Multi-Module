package jsl.moum.community.perform.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.moum.lifecycle.domain.LifecycleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static jsl.moum.community.perform.domain.entity.QPerformArticleEntity.performArticleEntity;
import static jsl.moum.moum.lifecycle.domain.QLifecycleEntity.lifecycleEntity;

@Repository
@RequiredArgsConstructor
public class PerformArticleRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * 이달의 공연 게시글 리스트 조회
     * SELECT *
     * FROM perform_article
     * WHERE performance_start_date BETWEEN DATE_FORMAT(NOW(), '%Y-%m-01') AND LAST_DAY(NOW())
     * ORDER BY created_at DESC;
     */
    public List<PerformArticleEntity> getThisMonthPerformArticles(int page, int size) {
        LocalDate startOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDate endOfMonth = startOfMonth.withDayOfMonth(startOfMonth.lengthOfMonth());

        return jpaQueryFactory
                .selectFrom(performArticleEntity)
                .where(
                        performArticleEntity.performanceStartDate.after(java.sql.Date.valueOf(startOfMonth))
                                .and(performArticleEntity.performanceStartDate.before(java.sql.Date.valueOf(endOfMonth)))
                )
                .orderBy(performArticleEntity.createdAt.desc())
                .offset((long) page * size)
                .limit(size)
                .fetch();
    }

    /**
     * 모음 이름으로 등록된 공연 게시글의 개수 세기
     * select COUNT(*)
     * from perform_article
     * where fk_moum_id =: moumId;
     */
    public boolean isAlreadyExistPerformArticleByMoumId(int moumId) {
        long count = jpaQueryFactory
                .selectFrom(performArticleEntity)
                .where(performArticleEntity.moum.id.eq(moumId))
                .fetch().size();

        return count > 0;
    }

    /**
     * 모음 ID로 모음의 공연게시글 찾기
     */
    public PerformArticleEntity findPerformArticleByMoumId(int moumId){
        return jpaQueryFactory
                .selectFrom(performArticleEntity)
                .where(performArticleEntity.moum.id.eq(moumId))
                .fetchOne();
    }
}
