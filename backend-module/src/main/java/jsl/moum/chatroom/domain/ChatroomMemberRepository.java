package jsl.moum.chatroom.domain;

import io.lettuce.core.dynamic.annotation.Param;
import jsl.moum.auth.domain.entity.MemberEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatroomMemberRepository extends JpaRepository<ChatroomMember, Integer> {
    List<ChatroomMember> findByMemberId(Integer memberId);

    @Query("SELECT cm.member.id FROM ChatroomMember cm WHERE cm.chatroom.id = :chatroomId")
    List<Integer> findAllMemberIdByChatroomId(@Param("chatroomId") Integer chatroomId);

    Optional<ChatroomMember> findByChatroomIdAndMemberId(Integer chatroomId, Integer memberId);

    @Query("SELECT cm1.chatroom.id " +
            "FROM ChatroomMember cm1 " +
            "JOIN ChatroomMember cm2 ON cm1.chatroom = cm2.chatroom " +
            "WHERE cm1.member = :member1 AND cm2.member = :member2")
    List<Integer> findCommonChatroomIds(@Param("member1") MemberEntity member1,
                                        @Param("member2") MemberEntity member2);
}
