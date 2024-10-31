package jsl.moum.backendmodule.auth.domain.entity;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QMemberEntity is a Querydsl query type for MemberEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QMemberEntity extends EntityPathBase<MemberEntity> {

    private static final long serialVersionUID = 1273738157L;

    public static final QMemberEntity memberEntity = new QMemberEntity("memberEntity");

    public final StringPath address = createString("address");

    public final StringPath email = createString("email");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final StringPath instrument = createString("instrument");

    public final StringPath name = createString("name");

    public final StringPath password = createString("password");

    public final StringPath proficiency = createString("proficiency");

    public final StringPath profileDescription = createString("profileDescription");

    public final StringPath profileImageUrl = createString("profileImageUrl");

    public final ListPath<jsl.moum.backendmodule.community.record.domain.MemberRecordEntity, jsl.moum.backendmodule.community.record.domain.QMemberRecordEntity> records = this.<jsl.moum.backendmodule.community.record.domain.MemberRecordEntity, jsl.moum.backendmodule.community.record.domain.QMemberRecordEntity>createList("records", jsl.moum.backendmodule.community.record.domain.MemberRecordEntity.class, jsl.moum.backendmodule.community.record.domain.QMemberRecordEntity.class, PathInits.DIRECT2);

    public final StringPath role = createString("role");

    public final ListPath<jsl.moum.backendmodule.moum.team.domain.TeamMemberEntity, jsl.moum.backendmodule.moum.team.domain.QTeamMemberEntity> teams = this.<jsl.moum.backendmodule.moum.team.domain.TeamMemberEntity, jsl.moum.backendmodule.moum.team.domain.QTeamMemberEntity>createList("teams", jsl.moum.backendmodule.moum.team.domain.TeamMemberEntity.class, jsl.moum.backendmodule.moum.team.domain.QTeamMemberEntity.class, PathInits.DIRECT2);

    public final StringPath username = createString("username");

    public QMemberEntity(String variable) {
        super(MemberEntity.class, forVariable(variable));
    }

    public QMemberEntity(Path<? extends MemberEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QMemberEntity(PathMetadata metadata) {
        super(MemberEntity.class, metadata);
    }

}

