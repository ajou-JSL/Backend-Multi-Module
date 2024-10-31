package jsl.moum.backendmodule.community.record.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QRecordEntity is a Querydsl query type for RecordEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRecordEntity extends EntityPathBase<RecordEntity> {

    private static final long serialVersionUID = 1398792669L;

    public static final QRecordEntity recordEntity = new QRecordEntity("recordEntity");

    public final DatePath<java.time.LocalDate> endDate = createDate("endDate", java.time.LocalDate.class);

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final ListPath<MemberRecordEntity, QMemberRecordEntity> members = this.<MemberRecordEntity, QMemberRecordEntity>createList("members", MemberRecordEntity.class, QMemberRecordEntity.class, PathInits.DIRECT2);

    public final StringPath recordName = createString("recordName");

    public final DatePath<java.time.LocalDate> startDate = createDate("startDate", java.time.LocalDate.class);

    public QRecordEntity(String variable) {
        super(RecordEntity.class, forVariable(variable));
    }

    public QRecordEntity(Path<? extends RecordEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRecordEntity(PathMetadata metadata) {
        super(RecordEntity.class, metadata);
    }

}

