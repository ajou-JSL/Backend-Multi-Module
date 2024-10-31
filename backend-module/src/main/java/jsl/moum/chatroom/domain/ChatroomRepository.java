package jsl.moum.chatroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatroomRepository extends JpaRepository<Chatroom, Integer> {
    List<Chatroom> findByMemberId(Integer memberId);
}
