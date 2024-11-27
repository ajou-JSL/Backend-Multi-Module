package jsl.moum.moum.team.service;

import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import jsl.moum.moum.lifecycle.domain.repository.LifecycleRepository;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleRepositoryCustom;
import jsl.moum.moum.team.domain.*;
import jsl.moum.objectstorage.StorageService;
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
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.team.dto.TeamDto;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class TeamServiceTest {

    @Spy
    @InjectMocks
    private TeamService teamService;

    @Mock
    private StorageService storageService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamMemberRepository teamMemberRepository;

    @Mock
    private TeamMemberRepositoryCustom teamMemberRepositoryCustom;

    @Mock
    private LifecycleRepositoryCustom lifecycleRepositoryCustom;

    @Mock
    private TeamRepositoryCustom teamRepositoryCustom;

    @Mock
    private LifecycleRepository lifecycleRepository;

    private MemberEntity mockLeader;
    private MemberEntity mockMember;
    private TeamEntity mockTeam;
    private TeamMemberEntity mockTeamMember;
    private LifecycleEntity mockLifecycle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockLeader = MemberEntity.builder()
                .id(1)
                .username("leader")
                .teams(new ArrayList<>())
                .records(new ArrayList<>())
                .build();

        mockMember = MemberEntity.builder()
                .id(2)
                .username("member")
                .teams(new ArrayList<>())
                .records(new ArrayList<>())
                .build();

        mockTeam = TeamEntity.builder()
                .id(1)
                .leaderId(mockLeader.getId())
                .teamName("Test Team")
                .description("Team Description")
                .members(new ArrayList<>())
                .records(new ArrayList<>())
                .build();

        mockTeamMember = TeamMemberEntity.builder()
                .id(1)
                .team(mockTeam)
                .member(mockMember)
                .build();

        mockLifecycle = LifecycleEntity.builder()
                .id(1)
                .lifecycleName("테스트 모음")
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
        assertThat(response.getTeamName()).isEqualTo(mockTeam.getTeamName());
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
                .hasMessage(ErrorCode.TEAM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("팀 리스트 조회 성공")
    void getTeamList_Success() {
        // given
        TeamEntity mockTeam2 = TeamEntity.builder()
                .id(2)
                .leaderId(mockLeader.getId())
                .teamName("Test Team2")
                .description("Team Description2")
                .members(new ArrayList<>())
                .records(new ArrayList<>())
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
    void createTeam_Success() throws IOException {
        // given
        TeamDto.Request teamRequestDto = TeamDto.Request.builder()
                .teamName("New Team")
                .description("New Team Description")
                .fileUrl("New Team fileUrl")
                .records(new ArrayList<>())
                .build();

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("new.jpg");
        when(file.isEmpty()).thenReturn(false);

        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(mockTeam);
        when(storageService.uploadFile(anyString(), any(MultipartFile.class)))
                .thenReturn("New Team fileUrl");

        // when
        TeamDto.Response response = teamService.createTeam(teamRequestDto, mockLeader.getUsername(), file);

        // then
        assertThat(response.getTeamName()).isEqualTo(teamRequestDto.getTeamName());
        assertThat(response.getDescription()).isEqualTo(teamRequestDto.getDescription());
        assertThat(response.getFileUrl()).isEqualTo(teamRequestDto.getFileUrl());
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
    void updateTeamInfo_Success() throws IOException {
        // given
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("existing fileUrl");
        when(file.isEmpty()).thenReturn(false);

        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(mockTeam);
        when(storageService.uploadFile(anyString(), any(MultipartFile.class)))
                .thenReturn("Updated fileUrl");

        doReturn(true).when(teamService).checkLeader(any(), any());
        doReturn(mockTeam).when(teamService).findTeam(anyInt());

        TeamDto.UpdateRequest teamUpdateRequestDto = TeamDto.UpdateRequest.builder()
                .teamName("Updated Team Name")
                .description("Updated Description")
                .fileUrl("Updated fileUrl")
                .build();

        // when
        TeamDto.UpdateResponse response = teamService.updateTeamInfo(mockTeam.getId(), teamUpdateRequestDto, mockLeader.getUsername(), file);

        // then
        assertThat(response.getTeamName()).isEqualTo("Updated Team Name");
        assertThat(response.getDescription()).isEqualTo("Updated Description");
        assertThat(response.getFileUrl()).isEqualTo("Updated fileUrl");
    }

    @Test
    @DisplayName("팀 정보 수정 실패 - 존재하지 않는 팀")
    void updateTeamInfo_Fail_TeamNotFound() throws IOException {
        // given
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("new.jpg");
        when(file.isEmpty()).thenReturn(false);
        when(storageService.uploadFile(anyString(), any(MultipartFile.class)))
                .thenReturn("new.jpg");

        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.empty());

        TeamDto.UpdateRequest teamUpdateRequestDto = TeamDto.UpdateRequest.builder()
                .teamName("Updated Team Name")
                .description("Updated Description")
                .build();

        // when & then
        assertThatThrownBy(() -> teamService.updateTeamInfo(mockTeam.getId(), teamUpdateRequestDto, mockLeader.getUsername(), file))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.TEAM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("팀 해체(삭제) 성공")
    void deleteTeamById_Success() {
        // given
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepositoryCustom.findLifecyclesByTeamId(anyInt())).thenReturn(List.of(mockLifecycle));

        // when
        TeamDto.Response response = teamService.deleteTeamById(mockTeam.getId(), mockLeader.getUsername());

        // then
        assertThat(response.getTeamName()).isEqualTo(mockTeam.getTeamName());
        verify(teamRepository).deleteById(mockTeam.getId());
        verify(lifecycleRepository).deleteAll(List.of(mockLifecycle));
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
    @DisplayName("팀 생성 실패 - 최대 생성 개수 초과")
    void create_team_fail_max_limit_exceeded() throws IOException {
        // given
        TeamDto.Request teamRequestDto = TeamDto.Request.builder()
                .teamName("New Team")
                .description("New Team Description")
                .fileUrl("New Team fileUrl")
                .records(new ArrayList<>())
                .build();

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("new.jpg");
        when(file.isEmpty()).thenReturn(false);

        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.save(any(TeamEntity.class))).thenReturn(mockTeam);
        when(storageService.uploadFile(anyString(), any(MultipartFile.class)))
                .thenReturn("New Team fileUrl");
        when(teamRepositoryCustom.countCreatedTeamByMemberId(mockLeader.getId())).thenReturn(3L);

        // when & then
        assertThatThrownBy(() -> teamService.createTeam(teamRequestDto, mockLeader.getUsername(),file))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.MAX_TEAM_LIMIT_EXCEEDED.getMessage());
    }

    @Test
    @DisplayName("팀 멤버 강퇴 성공")
    void kickMember_Success() {
        // given
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        when(teamMemberRepositoryCustom.existsByTeamAndMember(mockTeam.getId(), mockMember.getId())).thenReturn(true);
        doReturn(true).when(teamService).checkLeader(any(), any());

        // when
        TeamDto.Response response = teamService.kickMemberById(mockMember.getId(), mockTeam.getId(), mockLeader.getUsername());

        // then
        assertThat(response.getMembers().size()).isEqualTo(0);
        verify(teamMemberRepositoryCustom).deleteMemberFromTeamById(anyInt(), anyInt());
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

        doReturn(false).when(teamService).checkLeader(any(), any());
        doReturn(true).when(teamService).isTeamMember(anyInt(), anyInt());

        // when
        TeamDto.Response response = teamService.leaveTeam(mockTeam.getId(), mockMember.getUsername());

        // then
        assertThat(response.getTeamName()).isEqualTo(mockTeam.getTeamName());
        verify(teamMemberRepositoryCustom).deleteMemberFromTeamById(anyInt(), anyInt());
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
    @Test
    @DisplayName("멤버가 자신이 속한 팀 목록 조회 성공")
    void get_team_list_byMemberId_success() {
        // given
        int memberId = mockMember.getId();

        TeamEntity mockTeam1 = TeamEntity.builder()
                .id(1)
                .leaderId(mockLeader.getId())
                .teamName("team one")
                .description("description one")
                .members(List.of(mockTeamMember))
                .records(new ArrayList<>())
                .build();

        TeamEntity mockTeam2 = TeamEntity.builder()
                .id(2)
                .leaderId(mockLeader.getId())
                .teamName("team two")
                .description("description two")
                .members(List.of(mockTeamMember))
                .records(new ArrayList<>())
                .build();

        List<TeamEntity> memberTeams = List.of(mockTeam1, mockTeam2);
        List<TeamDto.Response> expectedResponseList = memberTeams.stream()
                .map(TeamDto.Response::new)
                .collect(Collectors.toList());

        // when
        when(teamService.isMemberExist(memberId)).thenReturn(true);
        when(teamMemberRepositoryCustom.findAllTeamsByMemberId(memberId)).thenReturn(expectedResponseList);

        // then
        List<TeamDto.Response> actualResponseList = teamService.getTeamsByMemberId(memberId);

        assertEquals(expectedResponseList.size(), actualResponseList.size());
        for (int i = 0; i < expectedResponseList.size(); i++) {
            assertEquals(expectedResponseList.get(i).getTeamName(), actualResponseList.get(i).getTeamName());
            assertEquals(expectedResponseList.get(i).getLeaderId(), actualResponseList.get(i).getLeaderId());
        }
    }

    @Test
    @DisplayName("멤버의 팀 목록 조회 실패 - 없는 유저")
    void get_team_list_byMemberId_fail_MemberNotFound() {
        // given
        int invalidMemberId = 411414;

        // when & then
        assertThatThrownBy(() -> teamService.getTeamsByMemberId(invalidMemberId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_EXIST.getMessage());
    }

}

