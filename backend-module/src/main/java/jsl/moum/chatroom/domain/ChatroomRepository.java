package jsl.moum.chatroom.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatroomRepository extends JpaRepository<Chatroom, Integer> {
//    List<Chatroom> findByMemberId(Integer memberId);

    Boolean existsByTeamId(Integer teamId);
}
