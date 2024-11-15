package jsl.moum.community.perform.domain.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

import static jsl.moum.community.perform.domain.entity.QPerformArticleEntity.performArticleEntity;

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
}
