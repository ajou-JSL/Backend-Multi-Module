package jsl.moum.chatappmodule.chat;

import org.springframework.data.r2dbc.repository.R2dbcRepository;
import reactor.core.publisher.Mono;

public interface ChatroomRepository extends R2dbcRepository<Chatroom, Integer> {
    Mono<Chatroom> findById(int chatroomId);
}
