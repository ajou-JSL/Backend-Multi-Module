package jsl.moum.backendmodule.moum.team.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamMemberEntity is a Querydsl query type for TeamMemberEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamMemberEntity extends EntityPathBase<TeamMemberEntity> {

    private static final long serialVersionUID = -299826832L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QTeamMemberEntity teamMemberEntity = new QTeamMemberEntity("teamMemberEntity");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final jsl.moum.backendmodule.auth.domain.entity.QMemberEntity member;

    public final QTeamEntity team;

    public QTeamMemberEntity(String variable) {
        this(TeamMemberEntity.class, forVariable(variable), INITS);
    }

    public QTeamMemberEntity(Path<? extends TeamMemberEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QTeamMemberEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QTeamMemberEntity(PathMetadata metadata, PathInits inits) {
        this(TeamMemberEntity.class, metadata, inits);
    }

    public QTeamMemberEntity(Class<? extends TeamMemberEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.member = inits.isInitialized("member") ? new jsl.moum.backendmodule.auth.domain.entity.QMemberEntity(forProperty("member")) : null;
        this.team = inits.isInitialized("team") ? new QTeamEntity(forProperty("team")) : null;
    }

}

