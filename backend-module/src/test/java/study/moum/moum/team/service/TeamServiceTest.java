package study.moum.moum.team.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.auth.dto.MemberDto;
import study.moum.global.error.ErrorCode;
import study.moum.global.error.exception.CustomException;
import study.moum.moum.team.domain.*;
import study.moum.moum.team.dto.TeamDto;

import java.lang.reflect.Member;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class TeamServiceTest {

    @Spy
    @InjectMocks
    private TeamService teamService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamMemberRepositoryCustom teamMemberRepositoryCustom;

    private MemberEntity mockLeader;
    private MemberEntity mockMember;
    private TeamEntity mockTeam;
    private TeamMemberEntity mockTeamMember;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockLeader = MemberEntity.builder()
                .id(1)
                .username("leader")
                .teams(new ArrayList<>())
                .build();

        mockMember = MemberEntity.builder()
                .id(2)
                .username("member")
                .teams(new ArrayList<>())
                .build();

        mockTeam = TeamEntity.builder()
                .id(1)
                .leaderId(mockLeader.getId())
                .teamname("Test Team")
                .description("Team Description")
                .members(new ArrayList<>())
                .build();

        mockTeamMember = TeamMemberEntity.builder()
                .id(1)
                .team(mockTeam)
                .member(mockMember)
                .build();
    }

    @Test
    @DisplayName("팀 ID로 팀 정보 조회 성공")
    void getTeamById_Success() {
        // given
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));

        // when
        TeamDto.Response response = teamService.getTeamById(mockTeam.getId());

        // then
        assertThat(response.getTeamName()).isEqualTo(mockTeam.getTeamname());
        assertThat(response.getDescription()).isEqualTo(mockTeam.getDescription());
    }

    @Test
    @DisplayName("팀 ID로 팀 정보 조회 실패 - 존재하지 않는 팀")
    void getTeamById_Fail_TeamNotFound() {
        // given
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> teamService.getTeamById(mockTeam.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ILLEGAL_ARGUMENT.getMessage());
    }

    @Test
    @DisplayName("팀 리스트 조회 성공")
    void getTeamList_Success() {
        // given
        TeamEntity mockTeam2 = TeamEntity.builder()
                .id(2)
                .leaderId(mockLeader.getId())
                .teamname("Test Team2")
                .description("Team Description2")
                .members(new ArrayList<>())
                .build();

        List<TeamEntity> mockTeams = List.of(mockTeam, mockTeam2);
        when(teamRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(mockTeams));

        // when
        List<TeamDto.Response> result = teamService.getTeamList(0,10);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getTeamName()).isEqualTo("Test Team");
        assertThat(result.get(1).getTeamName()).isEqualTo("Test Team2");
    }

    @Test
    @DisplayName("팀 생성 성공")
    void createTeam_Success() {
        // given
        TeamDto.Request teamRequestDto = TeamDto.Request.builder()
                .teamname("New Team")
                .description("New Team Description")
                .build();

        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(mockTeam);

        // when
        TeamDto.Response response = teamService.createTeam(teamRequestDto, mockLeader.getUsername());

        // then
        assertThat(response.getTeamName()).isEqualTo(teamRequestDto.getTeamname());
        assertThat(response.getDescription()).isEqualTo(teamRequestDto.getDescription());
    }

    @Test
    @DisplayName("팀 초대 성공")
    void inviteMember_Success() {
        // given
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(teamMemberRepositoryCustom.existsByTeamAndMember(mockTeam.getId(), mockMember.getId())).thenReturn(false);
        when(teamMemberRepository.save(any(TeamMemberEntity.class))).thenReturn(new TeamMemberEntity());

        // when
        MemberDto.Response response = teamService.inviteMember(mockTeam.getId(), mockMember.getId(), mockLeader.getUsername());

        // then
        assertThat(response.getUsername()).isEqualTo(mockMember.getUsername());
    }

    @Test
    @DisplayName("팀 초대 실패 - 존재하지 않는 팀")
    void inviteMember_Fail_TeamNotFound() {
        // given
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> teamService.inviteMember(mockTeam.getId(), mockMember.getId(), mockLeader.getUsername()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.TEAM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("팀 초대 실패 - 리더가 아님")
    void inviteMember_Fail_NotLeader() {
        // given
        when(memberRepository.findByUsername(mockMember.getUsername())).thenReturn(mockMember);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));

        doReturn(false).when(teamService).checkLeader(any(), any());

        // when & then
        assertThatThrownBy(() -> teamService.inviteMember(mockTeam.getId(), mockMember.getId(), mockMember.getUsername()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NO_AUTHORITY.getMessage());
    }

    @Test
    @DisplayName("팀 정보 수정 성공")
    void updateTeamInfo_Success() {
        // given
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(mockTeam);

        doReturn(true).when(teamService).checkLeader(any(), any());
        doReturn(mockTeam).when(teamService).findTeam(anyInt());

        TeamDto.UpdateRequest teamUpdateRequestDto = TeamDto.UpdateRequest.builder()
                .teamname("Updated Team Name")
                .description("Updated Description")
                .build();

        // when
        TeamDto.UpdateResponse response = teamService.updateTeamInfo(mockTeam.getId(), teamUpdateRequestDto, mockLeader.getUsername());

        // then
        assertThat(response.getTeamName()).isEqualTo("Updated Team Name");
        assertThat(response.getDescription()).isEqualTo("Updated Description");
    }

    @Test
    @DisplayName("팀 정보 수정 실패 - 존재하지 않는 팀")
    void updateTeamInfo_Fail_TeamNotFound() {
        // given
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.empty());

        TeamDto.UpdateRequest teamUpdateRequestDto = TeamDto.UpdateRequest.builder()
                .teamname("Updated Team Name")
                .description("Updated Description")
                .build();

        // when & then
        assertThatThrownBy(() -> teamService.updateTeamInfo(mockTeam.getId(), teamUpdateRequestDto, mockLeader.getUsername()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.TEAM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("팀 해체(삭제) 성공")
    void deleteTeamById_Success() {
        // given
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));


        // when
        TeamDto.Response response = teamService.deleteTeamById(mockTeam.getId(), mockLeader.getUsername());

        // then
        assertThat(response.getTeamName()).isEqualTo(mockTeam.getTeamname());
        verify(teamRepository).deleteById(mockTeam.getId());
    }

    @Test
    @DisplayName("팀 해체 실패 - 리더가 아님")
    void deleteTeamById_Fail_NotLeader() {
        // given
        when(memberRepository.findByUsername(mockMember.getUsername())).thenReturn(mockMember);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));

        doReturn(false).when(teamService).checkLeader(any(), any());

        // when & then
        assertThatThrownBy(() -> teamService.deleteTeamById(mockTeam.getId(), mockMember.getUsername()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NO_AUTHORITY.getMessage());
    }

    @Test
    @DisplayName("팀 멤버 강퇴 성공")
    @Disabled("테스트코드 수정 필요")
    void kickMember_Success() {
        // given
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(teamMemberRepositoryCustom.existsByTeamAndMember(mockTeam.getId(), mockMember.getId())).thenReturn(true);
        doReturn(true).when(teamService).checkLeader(any(), any());
        when(teamMemberRepositoryCustom.findMemberInTeamById(anyInt(), anyInt())).thenReturn(mockTeamMember);

        // when
        TeamDto.Response response = teamService.kickMemberById(mockMember.getId(), mockTeam.getId(), mockLeader.getUsername());

        // then
        assertThat(response.getMembers().size()).isEqualTo(0);
        verify(teamMemberRepositoryCustom).deleteMemberFromTeamById(anyInt(), anyInt());
        verify(teamRepository).save(mockTeam);
        verify(memberRepository).save(mockMember);
    }

    @Test
    @DisplayName("팀 멤버 강퇴 실패 - 존재하지 않는 팀")
    void kickMember_Fail_TeamNotFound() {
        // given
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> teamService.kickMemberById(mockMember.getId(), mockTeam.getId(), mockLeader.getUsername()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.TEAM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("팀 멤버 강퇴 실패 - 팀 멤버가 아님")
    void kickMember_Fail_NotTeamMember() {
        // given
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);

        doReturn(true).when(teamService).checkLeader(mockTeam, mockMember);
        doReturn(false).when(teamService).isTeamMember(anyInt(), anyInt());

        // when & then
        assertThatThrownBy(() -> teamService.kickMemberById(mockMember.getId(), mockTeam.getId(), mockLeader.getUsername()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_TEAM_MEMBER.getMessage());
    }

    @Test
    @DisplayName("팀 멤버 강퇴 실패 - 리더가 아님")
    void kickMember_Fail_NotLeader() {
        // given
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(memberRepository.findById(mockLeader.getId())).thenReturn(Optional.of(mockLeader));
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));

        doReturn(true).when(teamService).isTeamMember(anyInt(), anyInt());
        doReturn(false).when(teamService).checkLeader(any(), any());

        // when & then
        assertThatThrownBy(() -> teamService.kickMemberById(mockMember.getId(), mockTeam.getId(), mockLeader.getUsername()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NO_AUTHORITY.getMessage());
    }

    @Test
    @DisplayName("팀 멤버 강퇴 실패 - 존재하지 않는 멤버")
    void kickMember_Fail_MemberNotFound() {
        // given
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.empty());
        when(memberRepository.findById(mockLeader.getId())).thenReturn(Optional.of(mockLeader));
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));

        doReturn(true).when(teamService).isTeamMember(anyInt(), anyInt());
        doReturn(true).when(teamService).checkLeader(any(), any());

        // when & then
        assertThatThrownBy(() -> teamService.kickMemberById(mockMember.getId(), mockTeam.getId(), mockLeader.getUsername()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_EXIST.getMessage());
    }

    @Test
    @DisplayName("팀에서 탈퇴 성공")
    void leaveTeam_Success() {
        // given
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(memberRepository.findByUsername(mockMember.getUsername())).thenReturn(mockMember);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(teamMemberRepositoryCustom.findMemberInTeamById(anyInt(), anyInt())).thenReturn(mockTeamMember);

        doReturn(false).when(teamService).checkLeader(any(), any());
        doReturn(true).when(teamService).isTeamMember(anyInt(), anyInt());

        // when
        TeamDto.Response response = teamService.leaveTeam(mockTeam.getId(), mockMember.getUsername());

        // then
        assertThat(response.getTeamName()).isEqualTo(mockTeam.getTeamname());
        verify(teamMemberRepositoryCustom).deleteMemberFromTeamById(anyInt(), anyInt());
        verify(teamRepository).save(mockTeam);
        verify(memberRepository).save(mockMember);
    }

    @Test
    @DisplayName("팀에서 탈퇴 실패 - 팀 미존재")
    void leaveTeam_Fail_TeamNotFound() {
        // given
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> teamService.leaveTeam(mockTeam.getId(), mockMember.getUsername()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.TEAM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("팀 탈퇴 실패 - 팀 멤버가 아님")
    void leaveTeam_Fail_MemberNotFound() {
        // given
        when(memberRepository.findByUsername(mockMember.getUsername())).thenReturn(mockMember);
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));

        doReturn(false).when(teamService).checkLeader(any(), any());
        doReturn(false).when(teamService).isTeamMember(mockTeam.getId(), mockMember.getId());


        // when & then
        assertThatThrownBy(() -> teamService.leaveTeam(mockTeam.getId(), mockMember.getUsername()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_TEAM_MEMBER.getMessage());

    }

    @Test
    @DisplayName("팀에서 탈퇴 실패 - 리더임")
    void leaveTeam_Fail_LeaderCannot() {
        // given
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));

        doReturn(true).when(teamService).checkLeader(any(), any());

        // when & then
        assertThatThrownBy(() -> teamService.leaveTeam(mockTeam.getId(), mockLeader.getUsername()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.LEADER_CANNOT_LEAVE.getMessage());
    }


    @Test
    @DisplayName("초대 요청 수락 성공")
    @Disabled("아직 미구현")
    void acceptInvite_Success() {

    }

    @Test
    @DisplayName("초대 요청 거절 성공")
    @Disabled("아직 미구현")
    void rejectInvite_Success() {

    }


}

