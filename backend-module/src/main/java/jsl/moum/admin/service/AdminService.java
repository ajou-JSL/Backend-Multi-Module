package jsl.moum.admin.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.business.domain.PerformanceHallRepository;
import jsl.moum.business.domain.PracticeRoomRepository;
import jsl.moum.chatroom.domain.ChatroomRepository;
import jsl.moum.chatroom.dto.ChatroomDto;
import jsl.moum.moum.team.domain.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminService {

    private final ChatroomRepository chatroomRepository;
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
     private final PracticeRoomRepository practiceRoomRepository;
     private final PerformanceHallRepository performanceHallRepository;

    public Long getChatroomCount() {
        return chatroomRepository.count();
    }

    public Long getMemberCount() {
        return memberRepository.count();
    }

    public Long getTeamCount() {
        return teamRepository.count();
    }

    public Long getPracticeRoomCount() {
        return practiceRoomRepository.count();
    }

    public Long getPerformanceHallCount() {
        return performanceHallRepository.count();
    }

    public List<ChatroomDto> getChatrooms(){
        return chatroomRepository.findAll().stream().map(ChatroomDto::new).toList();
    }

    public List<MemberDto.Response> getMembers(){
        return memberRepository.findAll().stream().map(MemberDto.Response::new).toList();
    }

    public MemberDto.Response getMemberById(int id){
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보가 존재하지 않습니다."));
        return new MemberDto.Response(member);
    }
}
