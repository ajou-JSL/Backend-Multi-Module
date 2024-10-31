package jsl.moum.backendmodule.moum.moum.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QMoumTeamEntity is a Querydsl query type for MoumTeamEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMoumTeamEntity extends EntityPathBase<MoumTeamEntity> {

    private static final long serialVersionUID = 2124456557L;

    public static final QMoumTeamEntity moumTeamEntity = new QMoumTeamEntity("moumTeamEntity");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public QMoumTeamEntity(String variable) {
        super(MoumTeamEntity.class, forVariable(variable));
    }

    public QMoumTeamEntity(Path<? extends MoumTeamEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMoumTeamEntity(PathMetadata metadata) {
        super(MoumTeamEntity.class, metadata);
    }

}

