package jsl.moum.chatroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatroomMemberRepository extends JpaRepository<ChatroomMember, Integer> {
    List<ChatroomMember> findByMemberId(Integer memberId);

    List<Integer> findMemberIdByChatroomId(Integer chatroomId);
}
