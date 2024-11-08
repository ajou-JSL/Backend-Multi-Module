package jsl.moum.chatappmodule.chat;

import org.springframework.data.r2dbc.repository.R2dbcRepository;

import java.util.Optional;

public interface ChatroomRepository extends R2dbcRepository<Chatroom, Integer> {
    Optional<Chatroom> findById(int chatroomId);
}
