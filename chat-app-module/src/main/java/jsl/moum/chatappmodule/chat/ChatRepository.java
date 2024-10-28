package jsl.moum.chatappmodule.chat;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {
    @Tailable
    @Query("{ chatroomId: ?0 }")
    Flux<Chat> mFindByChatroomId(int chatroomId);

    @Tailable
    @Query("{ chatroomId: ?0, timestamp: { $gt: ?1 } }")
    Flux<Chat> mFindByChatroomIdAndTimestampAfter(int chatroomId, LocalDateTime timestamp);

    @Query("{ chatroomId: ?0, timestamp: { $lt: ?1 } }")
    Flux<Chat> findOlderChatsByChatroomId(int chatroomId, LocalDateTime timestamp, PageRequest pageRequest);

    Flux<Chat> findByChatroomId(int chatroomId, PageRequest pageRequest);
}
