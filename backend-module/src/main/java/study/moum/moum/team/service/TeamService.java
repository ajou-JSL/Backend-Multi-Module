package study.moum.moum.team.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.auth.dto.MemberDto;
import study.moum.community.article.domain.article.ArticleEntity;
import study.moum.community.article.dto.ArticleDto;
import study.moum.global.error.ErrorCode;
import study.moum.global.error.exception.CustomException;
import study.moum.global.error.exception.NeedLoginException;
import study.moum.moum.team.domain.*;
import study.moum.moum.team.dto.TeamDto;

import java.sql.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TeamService {

    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;
    private final TeamMemberRepository teamMemberRepository;
    private final TeamMemberRepositoryCustom teamMemberRepositoryCustom;

    /**
        팀 정보 조회
     **/
    @Transactional(readOnly = true)
    public TeamDto.Response getTeamById(int teamId){

        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(()-> new CustomException(ErrorCode.ILLEGAL_ARGUMENT));

        return new TeamDto.Response(team);
    }

    /**
        팀 리스트 조회
     **/
    @Transactional(readOnly = true)
    public List<TeamDto.Response> getTeamList(int page, int size){

        List<TeamEntity> teams = teamRepository.findAll(PageRequest.of(page, size)).getContent();

        List<TeamDto.Response> teamsList = teams.stream()
                .map(TeamDto.Response::new)
                .collect(Collectors.toList());

        return teamsList;
    }

    /**
     * 팀 생성
     */
    @Transactional
    public TeamDto.Response createTeam(TeamDto.Request teamRequestDto, String username){

        MemberEntity loginUser = memberRepository.findByUsername(username);

        TeamDto.Request request = TeamDto.Request.builder()
                .members(new ArrayList<>())
                .teamname(teamRequestDto.getTeamname())
                .description(teamRequestDto.getDescription())
                .leaderId(loginUser.getId())
                .build();

        TeamEntity newTeam = request.toEntity();
        teamRepository.save(newTeam);

        TeamMemberEntity teamMember = TeamMemberEntity.builder()
                .team(newTeam)
                .member(loginUser)
                .build();

        teamMemberRepository.save(teamMember);

        return new TeamDto.Response(newTeam);
    }


    /**
     * 팀에 멤버 초대
     */
    @Transactional
    public MemberDto.Response inviteMember(int teamId, int targetMemberId, String username) {

        MemberEntity loginUser = memberRepository.findByUsername(username);
        TeamEntity team = findTeam(teamId);

        // 팀의 리더인지 확인
        if(!checkLeader(team, loginUser)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        // 멤버 찾기
        MemberEntity targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_EXIST));

        // 이미 팀 멤버면 에러
        if(isTeamMember(teamId, targetMemberId)){
            throw new CustomException(ErrorCode.MEMBER_ALREADY_INVITED);
        }

        // 팀 멤버 초대 로직
        TeamMemberEntity teamMember = TeamMemberEntity.builder()
                .member(targetMember)
                .team(team)
                .build();

        // 팀 멤버 저장
        teamMemberRepository.save(teamMember);

        return new MemberDto.Response(targetMember); // 팀 정보 반환
    }


    /**
     * 팀 정보 수정 메소드
     */
    @Transactional
    public TeamDto.UpdateResponse updateTeamInfo(int teamId, TeamDto.UpdateRequest teamUpdateRequestDto, String username) {

        MemberEntity leader = memberRepository.findByUsername(username);
        TeamEntity team = findTeam(teamId);

        if(!checkLeader(team, leader)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        TeamDto.UpdateRequest request = TeamDto.UpdateRequest.builder()
                .teamname(teamUpdateRequestDto.getTeamname())
                .description(teamUpdateRequestDto.getDescription())
                .build();

        TeamEntity updatedTeam = request.toEntity();
        teamRepository.save(updatedTeam);

        return new TeamDto.UpdateResponse(updatedTeam);

    }

    /**
     * 팀 해체(삭제) 메소드
     */
    @Transactional
    public TeamDto.Response deleteTeamById(int teamId, String username) {
        MemberEntity leader = memberRepository.findByUsername(username);
        TeamEntity targetTeam = findTeam(teamId);

        if(!checkLeader(targetTeam, leader)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

//        // 팀의 멤버 목록을 가져와서 각 멤버의 팀 리스트에서 해당 팀을 삭제
//        for (TeamMemberEntity teamMember : targetTeam.getMembers()) {
//            MemberEntity member = teamMember.getMember();
//            member.removeTeamFromMember(targetTeam);
//        }

        teamRepository.deleteById(teamId);
        teamMemberRepositoryCustom.deleteTeamMemberTable(teamId);

        return new TeamDto.Response(targetTeam);

    }


    /**
     * 팀에서 멤버 강퇴 메소드
     */
    // todo : 버그해결. 테스트필요
    // todo : 요청을 보내면, 리더가 아니면 에러가남 -> 리더인 멤버만 강퇴 가능해지는 버그. 타겟멤버랑 로그인멤버 구분 필요
    @Transactional
    public TeamDto.Response kickMemberById(int targetMemberId, int teamId, String username) {

        // 타겟멤버
        MemberEntity targetMember = memberRepository.findById(targetMemberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_EXIST));
        TeamEntity team = findTeam(teamId);

        MemberEntity leader = memberRepository.findByUsername(username);
        // 로그인 유저가 리더 아니면 에러. 대상이 리더가 아니라.
        if(!checkLeader(team,leader)){
            throw new CustomException(ErrorCode.NO_AUTHORITY);
        }

        // 강퇴 대상 멤버 찾기
        if(!isMemberExist(targetMemberId)){
            throw new CustomException(ErrorCode.MEMBER_NOT_EXIST);
        }

        // 팀 멤버가 아니면 에러
        if(!isTeamMember(teamId, targetMemberId)){
            throw new CustomException(ErrorCode.NOT_TEAM_MEMBER);
        }

        // 강퇴 대상 멤버가 팀의 멤버인지 확인
        TeamMemberEntity teamMember = teamMemberRepositoryCustom.findMemberInTeamById(teamId, targetMemberId);

        // 팀에서 멤버 제거
        team.removeMemberFromTeam(teamMember);
        // 멤버에서 팀 제거
        targetMember.removeTeamFromMember(team);


        teamMemberRepositoryCustom.deleteMemberFromTeamById(teamId, targetMemberId);
        teamRepository.save(team);
        memberRepository.save(targetMember);

        return new TeamDto.Response(team);
    }

    /**
     * 팀에서 탈퇴 메소드
     */
    @Transactional
    public TeamDto.Response leaveTeam(int teamId, String username) {
        MemberEntity member = memberRepository.findByUsername(username);
        TeamEntity team = findTeam(teamId);

        if(checkLeader(team, member)){
            throw new CustomException(ErrorCode.LEADER_CANNOT_LEAVE);
        }

        if(!isTeamMember(teamId, member.getId())){
            throw new CustomException(ErrorCode.NOT_TEAM_MEMBER);
        }


        TeamMemberEntity teamMember = teamMemberRepositoryCustom.findMemberInTeamById(teamId, member.getId());

        // 팀에서 멤버 제거
        team.removeMemberFromTeam(teamMember);
        // 멤버에서 팀 제거
        member.removeTeamFromMember(team);


        teamMemberRepositoryCustom.deleteMemberFromTeamById(teamId, member.getId());
        teamRepository.save(team);
        memberRepository.save(member);
        return new TeamDto.Response(team);
    }


    /**
     * 유저로부터 온 초대 요청 수락 API
     *
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @param
     *
     */
    @Transactional
    public void 초대요청수락(){
    }


    /**
     * 유저로부터 온 초대 요청 거절 API
     *
     * @param customUserDetails 현재 인증된 사용자 정보 (CustomUserDetails 객체에서 사용자 정보 추출)
     * @param
     *
     */
    @Transactional
    public void 초대요청거절(){
    }

    public TeamEntity findTeam(int teamId){
        TeamEntity team = teamRepository.findById(teamId)
                .orElseThrow(()-> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        return team;
    }

    public Boolean checkLeader(TeamEntity team, MemberEntity loginUser){
        if (team.getLeaderId() != loginUser.getId()) {
            return false;
        }

        return true;
    }

    public Boolean isTeamMember(int teamId, int memberId){
        boolean isAlreadyMember = teamMemberRepositoryCustom.existsByTeamAndMember(teamId, memberId);
        if (isAlreadyMember) {
            return true;
        }
        return false;
    }

    public Boolean isMemberExist(String username) {
        MemberEntity member = memberRepository.findByUsername(username);
        if(member == null){
            return false;
        }
        return true;
    }

    public Boolean isMemberExist(int memberId) {
        Boolean member = memberRepository.findById(memberId).isPresent();
        if(!member){
            return false;
        }
        return true;
    }
}
