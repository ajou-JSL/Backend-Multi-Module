package jsl.moum.admin.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.business.domain.PerformanceHall;
import jsl.moum.business.domain.PerformanceHallRepository;
import jsl.moum.business.domain.PracticeRoom;
import jsl.moum.business.domain.PracticeRoomRepository;
import jsl.moum.business.dto.PerformanceHallDto;
import jsl.moum.business.dto.PracticeRoomDto;
import jsl.moum.chatroom.domain.Chatroom;
import jsl.moum.chatroom.domain.ChatroomRepository;
import jsl.moum.chatroom.dto.ChatroomDto;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamRepository;
import jsl.moum.moum.team.dto.TeamDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    public Page<ChatroomDto> getChatroomsPaged(PageRequest pageRequest){
        return chatroomRepository.findAll(pageRequest).map(ChatroomDto::new);
    }

    public ChatroomDto getChatroomById(int id){
        Chatroom chatroom = chatroomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 채팅방 정보가 존재하지 않습니다."));
        return new ChatroomDto(chatroom);
    }

    public List<MemberDto.Response> getMembers(){
        return memberRepository.findAll().stream().map(MemberDto.Response::new).toList();
    }

    public Page<MemberEntity> getMembersPaged(PageRequest pageRequest){
        return memberRepository.findAll(pageRequest);
    }

    public MemberDto.Response getMemberById(int id){
        MemberEntity member = memberRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 회원 정보가 존재하지 않습니다."));
        return new MemberDto.Response(member);
    }

    public List<TeamDto.Response> getTeams(){
        return teamRepository.findAll().stream().map(TeamDto.Response::new).toList();
    }

    public TeamDto.Response getTeamById(int id){
        TeamEntity team = teamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 팀 정보가 존재하지 않습니다."));
        return new TeamDto.Response(team);
    }

    public List<PerformanceHallDto> getPerformanceHalls(){
        return performanceHallRepository.findAll().stream().map(PerformanceHallDto::new).toList();
    }

    public PerformanceHallDto getPerformanceHallById(int id){
        PerformanceHall hall = performanceHallRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 공연장 정보가 존재하지 않습니다."));
        return new PerformanceHallDto(hall);
    }

    public List<PracticeRoomDto> getPracticeRooms(){
        return practiceRoomRepository.findAll().stream().map(PracticeRoomDto::new).toList();
    }

    public PracticeRoomDto getPracticeRoomById(int id){
        PracticeRoom room = practiceRoomRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("해당 연습실 정보가 존재하지 않습니다."));
        return new PracticeRoomDto(room);
    }
}
