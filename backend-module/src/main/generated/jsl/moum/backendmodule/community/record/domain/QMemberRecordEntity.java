package jsl.moum.backendmodule.community.record.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberRecordEntity is a Querydsl query type for MemberRecordEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberRecordEntity extends EntityPathBase<MemberRecordEntity> {

    private static final long serialVersionUID = -828174633L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QMemberRecordEntity memberRecordEntity = new QMemberRecordEntity("memberRecordEntity");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final jsl.moum.backendmodule.auth.domain.entity.QMemberEntity member;

    public final QRecordEntity record;

    public QMemberRecordEntity(String variable) {
        this(MemberRecordEntity.class, forVariable(variable), INITS);
    }

    public QMemberRecordEntity(Path<? extends MemberRecordEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QMemberRecordEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QMemberRecordEntity(PathMetadata metadata, PathInits inits) {
        this(MemberRecordEntity.class, metadata, inits);
    }

    public QMemberRecordEntity(Class<? extends MemberRecordEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new jsl.moum.backendmodule.auth.domain.entity.QMemberEntity(forProperty("member")) : null;
        this.record = inits.isInitialized("record") ? new QRecordEntity(forProperty("record")) : null;
    }

}

