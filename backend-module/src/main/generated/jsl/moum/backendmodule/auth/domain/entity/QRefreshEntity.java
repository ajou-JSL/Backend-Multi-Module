package jsl.moum.backendmodule.auth.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QRefreshEntity is a Querydsl query type for RefreshEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QRefreshEntity extends EntityPathBase<RefreshEntity> {

    private static final long serialVersionUID = 1770319886L;

    public static final QRefreshEntity refreshEntity = new QRefreshEntity("refreshEntity");

    public final StringPath expiration = createString("expiration");

    public final NumberPath<Long> id = createNumber("id", Long.class);

    public final StringPath refresh = createString("refresh");

    public final StringPath username = createString("username");

    public QRefreshEntity(String variable) {
        super(RefreshEntity.class, forVariable(variable));
    }

    public QRefreshEntity(Path<? extends RefreshEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QRefreshEntity(PathMetadata metadata) {
        super(RefreshEntity.class, metadata);
    }

}

