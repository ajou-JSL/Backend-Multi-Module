package jsl.moum.moum.team.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.custom.WithAuthUser;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import jsl.moum.moum.team.dto.TeamDto;
import jsl.moum.moum.team.service.TeamService;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(TeamController.class)
class TeamControllerTest {

    @MockBean
    private TeamService teamService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private TeamDto.Request teamRequestDto;
    private MemberEntity mockMember;
    private MemberEntity mockLeader;
    private TeamEntity mockTeam;
    private TeamMemberEntity mockTeamMember;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        mockLeader = MemberEntity.builder()
                .id(1)
                .email("leader@mail.com")
                .username("mockLeader")
                .records(new ArrayList<>())
                .build();

        mockMember = MemberEntity.builder()
                .id(2)
                .email("member@mail.com")
                .username("mockMember")
                .records(new ArrayList<>())
                .build();

        mockTeam = TeamEntity.builder()
                .id(1)
                .leaderId(mockLeader.getId())
                .teamName("mock team")
                .description("mock description")
                .members(new ArrayList<>())
                .records(new ArrayList<>())
                .build();

        teamRequestDto = TeamDto.Request.builder()
                .leaderId(mockLeader.getId())
                .fileUrl("fileUrl")
                .teamName("mock team")
                .description("mock description")
                .members(new ArrayList<>())
                .records(new ArrayList<>())
                .build();

        mockTeamMember = TeamMemberEntity.builder()
                .member(mockMember)
                .team(mockTeam)
                .build();


    }


    @Test
    @DisplayName("팀 생성 테스트")
    @WithAuthUser(email = "leader@mail.com", username = "mockLeader")
    void create_team_success() throws Exception {
        // given
        TeamEntity mockTeamEntity = teamRequestDto.toEntity();
        TeamDto.Response response = new TeamDto.Response(mockTeamEntity);

        // when
       // when(teamService.createTeam(any(TeamDto.Request.class),any(String.class), any())).thenReturn(response);
        when(teamService.createTeam(any(),anyString(), any())).thenReturn(response);

        MockMultipartFile file = new MockMultipartFile("file", "testfile.jpg", MediaType.IMAGE_JPEG_VALUE, "test file content".getBytes());

        MockMultipartFile teamRequestDtoFile = new MockMultipartFile("teamRequestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(teamRequestDto).getBytes());

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/teams")
                        .file(file)
                        .file(teamRequestDtoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.CREATE_TEAM_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.teamName").value("mock team"))
                .andExpect(jsonPath("$.data.fileUrl").value("fileUrl"));

    }

    @Test
    @DisplayName("팀 단건 조회 테스트")
    @WithAuthUser
    void get_teamById_success() throws Exception {
        // given
        TeamDto.Response responseDto = new TeamDto.Response(mockTeam);

        // when
        when(teamService.getTeamById(anyInt())).thenReturn(responseDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/teams/{teamId}", mockTeam.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(ResponseCode.GET_TEAM_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.teamName").value("mock team"));
    }


    @Test
    @DisplayName("모든 팀 리스트 조회 테스트")
    @WithAuthUser
    void get_team_list_success() throws Exception {
        // given
        TeamEntity mockTeam2 = TeamEntity.builder()
                .id(2)
                .leaderId(mockLeader.getId())
                .teamName("another team")
                .description("another description")
                .members(new ArrayList<>())
                .records(new ArrayList<>())
                .build();

        List<TeamEntity> teams = new ArrayList<>();
        teams.add(mockTeam);
        teams.add(mockTeam2);

        List<TeamDto.Response> responseList = new ArrayList<>();
        for (TeamEntity t : teams) {
            responseList.add(new TeamDto.Response(t));
        }

        // when
        when(teamService.getTeamList(0,10)).thenReturn(responseList);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/teams-all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(ResponseCode.GET_TEAM_LIST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(responseList.size()))
                .andExpect(jsonPath("$.data[0].teamName").value(mockTeam.getTeamName()))
                .andExpect(jsonPath("$.data[0].leaderId").value(mockTeam.getLeaderId()))
                .andExpect(jsonPath("$.data[1].teamName").value(mockTeam2.getTeamName()))
                .andExpect(jsonPath("$.data[1].leaderId").value(mockTeam2.getLeaderId()));
    }


    @Test
    @DisplayName("팀 정보 수정 테스트")
    @WithAuthUser
    void update_team_info_success() throws Exception{
        // given
        TeamDto.UpdateRequest updateRequest = TeamDto.UpdateRequest.builder()
                .teamName("update team")
                .description("update description")
                .fileUrl("update fileUrl")
                .records(new ArrayList<>())
                .build();

        TeamEntity updateMockTeam = updateRequest.toEntity();
        TeamDto.UpdateResponse response = new TeamDto.UpdateResponse(updateMockTeam);

        // when
        //when(teamService.updateTeamInfo(mockTeam.getId(), updateRequest, mockLeader.getUsername())).thenReturn(response);
        when(teamService.updateTeamInfo(anyInt(), any(), anyString(), any())).thenReturn(response);

        MockMultipartFile file = new MockMultipartFile("file", "testfile.jpg", MediaType.IMAGE_JPEG_VALUE, "test file content".getBytes());

        MockMultipartFile teamRequestDtoFile = new MockMultipartFile("updateRequestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(teamRequestDto).getBytes());

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PATCH, "/api/teams/{teamId}", mockTeam.getId())
                        .file(file)
                        .file(teamRequestDtoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.UPDATE_TEAM_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.teamName").value(updateRequest.getTeamName()))
                .andExpect(jsonPath("$.data.description").value(updateRequest.getDescription()))
                .andExpect(jsonPath("$.data.fileUrl").value(updateRequest.getFileUrl()));
    }


    @Test
    @DisplayName("팀 삭제 테스트")
    @WithAuthUser
    void delete_team_success() throws Exception{
        // given
        int targetTeamId = mockTeam.getId();
        String leaderName = mockLeader.getUsername();

        TeamDto.Response response = new TeamDto.Response(mockTeam);

        // when
        //when(teamService.deleteTeamById(targetTeamId,leaderName)).thenReturn(response);
        when(teamService.deleteTeamById(anyInt(),anyString())).thenReturn(response);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/teams/{teamId}", targetTeamId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(ResponseCode.DELETE_TEAM_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("팀에 멤버 초대 테스트")
    @WithAuthUser
    void invite_member_success() throws Exception{
        // given
        int teamId = mockTeam.getId();

        MemberEntity targetMember = MemberEntity.builder()
                .id(222)
                .username("new member")
                .records(new ArrayList<>())
                .build();

        MemberDto.Response memberResponse = new MemberDto.Response(targetMember);

        // when
        when(teamService.inviteMember(anyInt(), anyInt(), anyString())).thenReturn(memberResponse);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/teams/{teamId}/invite/{memberId}", teamId, targetMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.INVITE_MEMBER_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.id").value(targetMember.getId()))
                .andExpect(jsonPath("$.data.username").value("new member"));
    }

    @Test
    @DisplayName("팀에서 멤버 강퇴 테스트")
    @WithAuthUser
    void kick_member_success() throws Exception{
        // given
        TeamDto.Response responseDto = new TeamDto.Response(mockTeam);

        // when
        when(teamService.kickMemberById(anyInt(), anyInt(), anyString())).thenReturn(responseDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/teams/{teamId}/kick/{memberId}",mockTeam.getId() ,mockMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(ResponseCode.KICK_MEMBER_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("팀에서 탈퇴 테스트")
    @WithAuthUser
    void leave_member_success() throws Exception{
        // given
        TeamDto.Response responseDto = new TeamDto.Response(mockTeam);

        // when
        when(teamService.leaveTeam(anyInt(), anyString())).thenReturn(responseDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/teams/leave/{teamId}",mockTeam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(ResponseCode.LEAVE_TEAM_SUCCESS.getMessage()));
    }
    @Test
    @DisplayName("멤버가 자신이 속한 팀 리스트 조회 테스트")
    @WithAuthUser
    void get_teams_by_member_success() throws Exception {
        // given
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

        List<TeamEntity> teams = List.of(mockTeam1, mockTeam2);

        List<TeamDto.Response> responseList = new ArrayList<>();
        for (TeamEntity team : teams) {
            responseList.add(new TeamDto.Response(team));
        }

        // when
        when(teamService.getTeamsByMemberId(anyInt())).thenReturn(responseList);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/teams-all/{memberId}", mockMember.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(ResponseCode.GET_TEAM_LIST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(responseList.size()))
                .andExpect(jsonPath("$.data[0].teamName").value(mockTeam1.getTeamName()))
                .andExpect(jsonPath("$.data[0].leaderId").value(mockTeam1.getLeaderId()))
                .andExpect(jsonPath("$.data[1].teamName").value(mockTeam2.getTeamName()))
                .andExpect(jsonPath("$.data[1].leaderId").value(mockTeam2.getLeaderId()));
    }




}