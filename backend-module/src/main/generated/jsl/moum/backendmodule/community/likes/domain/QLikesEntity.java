package jsl.moum.backendmodule.community.likes.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QLikesEntity is a Querydsl query type for LikesEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QLikesEntity extends EntityPathBase<LikesEntity> {

    private static final long serialVersionUID = -302185149L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QLikesEntity likesEntity = new QLikesEntity("likesEntity");

    public final jsl.moum.backendmodule.community.article.domain.article.QArticleEntity article;

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final jsl.moum.backendmodule.auth.domain.entity.QMemberEntity member;

    public QLikesEntity(String variable) {
        this(LikesEntity.class, forVariable(variable), INITS);
    }

    public QLikesEntity(Path<? extends LikesEntity> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QLikesEntity(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QLikesEntity(PathMetadata metadata, PathInits inits) {
        this(LikesEntity.class, metadata, inits);
    }

    public QLikesEntity(Class<? extends LikesEntity> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.article = inits.isInitialized("article") ? new jsl.moum.backendmodule.community.article.domain.article.QArticleEntity(forProperty("article"), inits.get("article")) : null;
        this.member = inits.isInitialized("member") ? new jsl.moum.backendmodule.auth.domain.entity.QMemberEntity(forProperty("member")) : null;
    }

}

