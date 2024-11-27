package jsl.moum.moum.lifecycle.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.global.error.exception.NeedLoginException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import jsl.moum.moum.lifecycle.dto.LifecycleDto;
import jsl.moum.moum.lifecycle.dto.ProcessDto;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
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
import jsl.moum.custom.WithAuthUser;
import jsl.moum.moum.lifecycle.service.LifecycleService;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(LifecycleController.class)
class LifecycleControllerTest {

    @InjectMocks
    private LifecycleController lifecycleController;

    @MockBean
    private LifecycleService lifecycleService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private LifecycleEntity mockLifecycle;
    private TeamMemberEntity mockTeamMember;
    private TeamEntity mockTeam;
    private MemberEntity mockLeader;
    private ProcessDto mockProcessDto;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        mockLeader = MemberEntity.builder()
                .id(1)
                .address("주소")
                .email("test@user.com")
                .teams(new ArrayList<>())
                .records(new ArrayList<>())
                .build();

        mockTeamMember = TeamMemberEntity.builder()
                .id(1)
                .leaderId(mockLeader.getId())
                .member(mockLeader)
                .team(mockTeam)
                .build();

        mockTeam = TeamEntity.builder()
                .id(1)
                .teamName("테스트 팀")
                .members(List.of(mockTeamMember))
                .records(new ArrayList<>())
                .build();


        mockLifecycle = LifecycleEntity.builder()
                .id(1)
                .team(mockTeam)
                .lifecycleName("테스트 라이프사이클")
                .records(new ArrayList<>())
                .build();

        mockProcessDto = ProcessDto.builder()
                .chatroomStatus(true)
                .recruitStatus(true)
                .build();

    }

    @Test
    @DisplayName("모음 조회")
    @WithAuthUser
    void get_lifecycle() throws Exception {
        // given
        int lifcycleId = mockLifecycle.getId();
        LifecycleDto.Response response = new LifecycleDto.Response(mockLifecycle);

        // when
        when(lifecycleService.getMoumById(anyString(), anyInt())).thenReturn(response);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/moum/{moumId}",lifcycleId)
                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(jsonPath("$.status").value(200))
                        .andExpect(jsonPath("$.data.moumName").value("테스트 라이프사이클"));

    }

    @Test
    @DisplayName("나의 라이프사이클 목록 조회")
    @WithAuthUser
    void get_my_lifecycle_list() throws Exception {
        // given
        LifecycleEntity anotherMockLifecycle = LifecycleEntity.builder()
                .id(2)
                .team(mockTeam)
                .lifecycleName("또 다른 라이프사이클")
                .records(new ArrayList<>())
                .build();
        List<LifecycleDto.Response> lifecycleList = List.of(
                new LifecycleDto.Response(mockLifecycle),
                new LifecycleDto.Response(anotherMockLifecycle)
        );

        // when
        when(lifecycleService.getMyMoumList(anyString())).thenReturn(lifecycleList);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/moum-all/my")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].moumName").value("테스트 라이프사이클"))
                .andExpect(jsonPath("$.data[0].moumId").value(1))
                .andExpect(jsonPath("$.data[1].moumName").value("또 다른 라이프사이클"))
                .andExpect(jsonPath("$.data[1].moumId").value(2));
    }

    @Test
    @DisplayName("팀의 라이프사이클 목록 조회")
    @WithAuthUser
    void get_team_lifecycle_list() throws Exception {
        // given
        LifecycleEntity anotherMockLifecycle = LifecycleEntity.builder()
                .id(2)
                .team(mockTeam)
                .lifecycleName("또 다른 라이프사이클")
                .records(new ArrayList<>())
                .build();
        List<LifecycleDto.Response> lifecycleList = List.of(
                new LifecycleDto.Response(mockLifecycle),
                new LifecycleDto.Response(anotherMockLifecycle)
        );

        // when
        when(lifecycleService.getTeamMoumList(anyInt())).thenReturn(lifecycleList);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/moum-all/team/{teamId}", mockTeam.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data[0].moumName").value("테스트 라이프사이클"))
                .andExpect(jsonPath("$.data[0].moumId").value(1))
                .andExpect(jsonPath("$.data[1].moumName").value("또 다른 라이프사이클"))
                .andExpect(jsonPath("$.data[1].moumId").value(2));
    }


    @Test
    @DisplayName("모음 생성")
    @WithAuthUser
    void create_lifecycle() throws Exception {
        // given
        LifecycleDto.Request requestDto = LifecycleDto.Request.builder()
                .teamId(mockTeam.getId())
                .moumName("테스트 라이프사이클")
                .records(new ArrayList<>())
                .build();
        // then
        LifecycleEntity lifecycle = requestDto.toEntity();
        lifecycle.assignTeam(mockTeam);
        LifecycleDto.Response response = new LifecycleDto.Response(lifecycle);

        // when
        // addLifecycle: (username, dto, file)
        when(lifecycleService.addMoum(anyString(), any(), any())).thenReturn(response);

        MockMultipartFile file = new MockMultipartFile("file", "testfile.jpg", MediaType.IMAGE_JPEG_VALUE, "test file content".getBytes());

        MockMultipartFile lifecycleRequestDtoFile = new MockMultipartFile("lifecycleRequestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(requestDto).getBytes());

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/moum")
                        .file(file)
                        .file(lifecycleRequestDtoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.CREATE_MOUM_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.moumName").value("테스트 라이프사이클"));
    }


    @Test
    @DisplayName("모음 정보 수정")
    @WithAuthUser
    void update_lifecycle_info() throws Exception {
        // given
        int targetId = mockLifecycle.getId();
        LifecycleDto.Request updateRequestDto = LifecycleDto.Request.builder()
                .teamId(mockTeam.getId())
                .moumName("업데이트 라이프사이클")
                .records(new ArrayList<>())
                .build();

        LifecycleEntity lifecycle = updateRequestDto.toEntity();
        lifecycle.assignTeam(mockTeam);
        LifecycleDto.Response response = new LifecycleDto.Response(lifecycle);

        // when
        when(lifecycleService.updateMoum(anyString(), any(), any(), anyInt())).thenReturn(response);

        MockMultipartFile file = new MockMultipartFile("file", "testfile.jpg", MediaType.IMAGE_JPEG_VALUE, "test file content".getBytes());

        MockMultipartFile lifecycleRequestDtoFile = new MockMultipartFile("lifecycleRequestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(updateRequestDto).getBytes());

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PATCH,"/api/moum/{moumId}", targetId )
                        .file(file)
                        .file(lifecycleRequestDtoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.UPDATE_MOUM_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.moumName").value("업데이트 라이프사이클"));
    }


    @Test
    @DisplayName("모음 삭제")
    @WithAuthUser
    void remove_lifecycle() throws Exception {
        // given
        int targetId = mockLifecycle.getId();
        LifecycleDto.Response responseDto = new LifecycleDto.Response(mockLifecycle);

        // when
        when(lifecycleService.deleteMoum(anyString(), anyInt())).thenReturn(responseDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/moum/{moumId}", targetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(ResponseCode.DELETE_MOUM_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.moumId").value(targetId))
                .andExpect(jsonPath("$.data.moumName").value("테스트 라이프사이클"));
    }


    @Test
    @DisplayName("모음 마감하기")
    @WithAuthUser
    void finish_lifecycle() throws Exception {
        // given
        String loginUser = mockLeader.getUsername();
        int targetId = mockLifecycle.getId();
        LifecycleDto.Response responseDto = new LifecycleDto.Response(mockLifecycle);

        // when
        when(lifecycleService.finishMoum(anyString(), anyInt())).thenReturn(responseDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/moum/finish/{moumId}", targetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.FINISH_MOUM_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("모음 되살리기")
    @WithAuthUser
    void reopen_lifecycle() throws Exception {
        // given
        String loginUser = mockLeader.getUsername();
        int targetId = mockLifecycle.getId();
        LifecycleDto.Response responseDto = new LifecycleDto.Response(mockLifecycle);

        // when
        when(lifecycleService.reopenMoum(anyString(), anyInt())).thenReturn(responseDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/moum/reopen/{moumId}", targetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.REOPEN_MOUM_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("모음 진행률 수정하기")
    @WithAuthUser
    void update_process_lifecycle() throws Exception {
        // given
        String username = mockLeader.getUsername();
        int targetId = mockLifecycle.getId();
        LifecycleDto.Response responseDto = new LifecycleDto.Response(mockLifecycle);

        // when
        //when(lifecycleService.updateProcessStatus(username, targetId, mockProcessDto)).thenReturn(responseDto);
        when(lifecycleService.updateProcessStatus(anyString(), anyInt(), any())).thenReturn(responseDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/moum/update-process/{moumId}", targetId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(responseDto))
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.UPDATE_MOUM_PROCESS_SUCCESS.getMessage()));
    }

    @Test
    @DisplayName("로그인 체크")
    void loginCheck_test(){
        // given
        String username = null;

        // when
        NeedLoginException exception = assertThrows(NeedLoginException.class, () -> {
            lifecycleController.loginCheck(username);
        });
//
//        // then
//        assertThatThrownBy(() -> lifecycleController.loginCheck(username))
//                .isInstanceOf(NeedLoginException.class)
//                .hasMessage(ErrorCode.NEED_LOGIN.getMessage());

        // then
        assertThat(exception.getMessage()).isEqualTo("로그인을 해야합니다.");
    }

}