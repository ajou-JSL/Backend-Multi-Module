package jsl.moum.admin.service;

import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.chatroom.domain.ChatroomRepository;
import jsl.moum.moum.team.domain.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final ChatroomRepository chatroomRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    // private final PracticeRoomRepository practiceRoomRepository;
    // private final PerformanceHallRepository performanceHallRepository;

    public Long getChatroomCount() {
        return chatroomRepository.count();
    }

    public Long getMemberCount() {
        return memberRepository.count();
    }

    public Long getTeamCount() {
        return teamRepository.count();
    }
}
