package jsl.moum.backendmodule.moum.team.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QTeamEntity is a Querydsl query type for TeamEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QTeamEntity extends EntityPathBase<TeamEntity> {

    private static final long serialVersionUID = -1641914250L;

    public static final QTeamEntity teamEntity = new QTeamEntity("teamEntity");

    public final NumberPath<Long> chatroomId = createNumber("chatroomId", Long.class);

    public final DateTimePath<java.time.LocalDateTime> createdAt = createDateTime("createdAt", java.time.LocalDateTime.class);

    public final StringPath description = createString("description");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> leaderId = createNumber("leaderId", Integer.class);

    public final ListPath<TeamMemberEntity, QTeamMemberEntity> members = this.<TeamMemberEntity, QTeamMemberEntity>createList("members", TeamMemberEntity.class, QTeamMemberEntity.class, PathInits.DIRECT2);

    public final StringPath teamname = createString("teamname");

    public QTeamEntity(String variable) {
        super(TeamEntity.class, forVariable(variable));
    }

    public QTeamEntity(Path<? extends TeamEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QTeamEntity(PathMetadata metadata) {
        super(TeamEntity.class, metadata);
    }

}

