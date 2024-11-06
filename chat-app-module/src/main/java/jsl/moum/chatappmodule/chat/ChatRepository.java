package jsl.moum.chatappmodule.chat;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;

import java.time.LocalDateTime;

@Repository
public interface ChatRepository extends ReactiveMongoRepository<Chat, String> {
    Flux<Chat> findByChatroomId(int chatroomId, PageRequest pageRequest);

    @Query("{ chatroomId: ?0, timestamp: { $lt: ?1 } }")
    Flux<Chat> findChatsBeforeTimestampByChatroomId(int chatroomId, LocalDateTime timestamp, PageRequest pageRequest);

}