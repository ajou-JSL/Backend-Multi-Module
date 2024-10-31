package jsl.moum.backendmodule.moum.moum.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMoumEntity is a Querydsl query type for MoumEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMoumEntity extends EntityPathBase<MoumEntity> {

    private static final long serialVersionUID = -1051042768L;

    public static final QMoumEntity moumEntity = new QMoumEntity("moumEntity");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public QMoumEntity(String variable) {
        super(MoumEntity.class, forVariable(variable));
    }

    public QMoumEntity(Path<? extends MoumEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMoumEntity(PathMetadata metadata) {
        super(MoumEntity.class, metadata);
    }

}

