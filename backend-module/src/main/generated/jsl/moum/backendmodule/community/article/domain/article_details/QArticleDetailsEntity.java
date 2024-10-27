package jsl.moum.backendmodule.community.article.domain.article_details;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QArticleDetailsEntity is a Querydsl query type for ArticleDetailsEntity
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QArticleDetailsEntity extends EntityPathBase<ArticleDetailsEntity> {

    private static final long serialVersionUID = 1335127418L;

    public static final QArticleDetailsEntity articleDetailsEntity = new QArticleDetailsEntity("articleDetailsEntity");

    public final NumberPath<Integer> articleId = createNumber("articleId", Integer.class);

    public final ListPath<jsl.moum.backendmodule.community.comment.domain.CommentEntity, jsl.moum.backendmodule.community.comment.domain.QCommentEntity> comments = this.<jsl.moum.backendmodule.community.comment.domain.CommentEntity, jsl.moum.backendmodule.community.comment.domain.QCommentEntity>createList("comments", jsl.moum.backendmodule.community.comment.domain.CommentEntity.class, jsl.moum.backendmodule.community.comment.domain.QCommentEntity.class, PathInits.DIRECT2);

    public final StringPath content = createString("content");

    public final StringPath fileUrl = createString("fileUrl");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public QArticleDetailsEntity(String variable) {
        super(ArticleDetailsEntity.class, forVariable(variable));
    }

    public QArticleDetailsEntity(Path<? extends ArticleDetailsEntity> path) {
        super(path.getType(), path.getMetadata());
    }

    public QArticleDetailsEntity(PathMetadata metadata) {
        super(ArticleDetailsEntity.class, metadata);
    }

}

