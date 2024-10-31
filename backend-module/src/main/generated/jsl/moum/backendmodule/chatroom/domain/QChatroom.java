package jsl.moum.backendmodule.chatroom.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QChatroom is a Querydsl query type for Chatroom
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QChatroom extends EntityPathBase<Chatroom> {

    private static final long serialVersionUID = -657492423L;

    public static final QChatroom chatroom = new QChatroom("chatroom");

    public final NumberPath<Integer> chatroomId = createNumber("chatroomId", Integer.class);

    public final StringPath chatroomName = createString("chatroomName");

    public final NumberPath<Integer> id = createNumber("id", Integer.class);

    public final NumberPath<Integer> memberId = createNumber("memberId", Integer.class);

    public QChatroom(String variable) {
        super(Chatroom.class, forVariable(variable));
    }

    public QChatroom(Path<? extends Chatroom> path) {
        super(path.getType(), path.getMetadata());
    }

    public QChatroom(PathMetadata metadata) {
        super(Chatroom.class, metadata);
    }

}

