package jsl.moum.chatroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChatroomMemberRepository extends JpaRepository<ChatroomMember, Integer> {

}
