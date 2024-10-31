package study.moum.moum.team.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.dto.MemberDto;
import study.moum.community.article.domain.article_details.ArticleDetailsEntity;
import study.moum.community.comment.controller.CommentController;
import study.moum.community.comment.domain.CommentEntity;
import study.moum.community.comment.dto.CommentDto;
import study.moum.community.comment.service.CommentService;
import study.moum.custom.WithAuthUser;
import study.moum.global.response.ResponseCode;
import study.moum.moum.team.domain.TeamEntity;
import study.moum.moum.team.domain.TeamMemberEntity;
import study.moum.moum.team.dto.TeamDto;
import study.moum.moum.team.service.TeamService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
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
    private TeamMemberEntity mockTeamMember1;
    private TeamMemberEntity mockTeamMember2;

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
                .build();

        mockMember = MemberEntity.builder()
                .id(2)
                .email("member@mail.com")
                .username("mockMember")
                .build();

        mockTeam = TeamEntity.builder()
                .id(1)
                .leaderId(mockLeader.getId())
                .teamname("mock team")
                .description("mock description")
                .members(new ArrayList<>())
                .build();

        teamRequestDto = TeamDto.Request.builder()
                .leaderId(mockLeader.getId())
                .teamname("mock team")
                .description("mock description")
                .members(new ArrayList<>())
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
        when(teamService.createTeam(any(TeamDto.Request.class),any(String.class))).thenReturn(response);
        //when(teamService.createTeam(any(),anyString())).thenReturn(response);


        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/teams")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teamRequestDto))
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.CREATE_TEAM_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.teamName").value("mock team"));
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
                .teamname("another team")
                .description("another description")
                .members(new ArrayList<>())
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
                .andExpect(jsonPath("$.data[0].teamName").value(mockTeam.getTeamname()))
                .andExpect(jsonPath("$.data[0].leaderId").value(mockTeam.getLeaderId()))
                .andExpect(jsonPath("$.data[1].teamName").value(mockTeam2.getTeamname()))
                .andExpect(jsonPath("$.data[1].leaderId").value(mockTeam2.getLeaderId()));
    }


    @Test
    @DisplayName("팀 정보 수정 테스트")
    @WithAuthUser
    void update_team_info_success() throws Exception{
        // given
        TeamDto.UpdateRequest updateRequest = TeamDto.UpdateRequest.builder()
                .teamname("update team")
                .description("update description")
                .build();

        TeamEntity updateMockTeam = updateRequest.toEntity();
        TeamDto.UpdateResponse response = new TeamDto.UpdateResponse(updateMockTeam);

        // when
        //when(teamService.updateTeamInfo(mockTeam.getId(), updateRequest, mockLeader.getUsername())).thenReturn(response);
        when(teamService.updateTeamInfo(anyInt(), any(), anyString())).thenReturn(response);

        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/teams/{teamId}", mockTeam.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest))
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.UPDATE_TEAM_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.teamName").value(updateRequest.getTeamname()))
                .andExpect(jsonPath("$.data.description").value(updateRequest.getDescription()));
    }

    @Test
    @DisplayName("나의 팀 리스트 조회 테스트")
    @WithAuthUser
    void get_my_team_list_success() throws Exception{

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




}