package jsl.moum.community.perform.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.community.perform.domain.entity.PerformMember;
import jsl.moum.community.perform.dto.PerformArticleDto;
import jsl.moum.community.perform.service.PerformArticleService;
import jsl.moum.custom.WithAuthUser;
import jsl.moum.global.error.exception.NeedLoginException;
import jsl.moum.global.response.ResponseCode;
import static org.junit.jupiter.api.Assertions.assertThrows;

import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import jsl.moum.moum.team.domain.TeamEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PerformArticleController.class)
class PerformArticleControllerTest {

    @InjectMocks
    private PerformArticleController performArticleController;

    @MockBean
    private PerformArticleService performArticleService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private PerformArticleEntity mockPerformArticle;
    private PerformArticleDto.Request mockPerformArticleRequestDto;
    private PerformMember mockPerformMemberA;
    private PerformMember mockPerformMemberB;
    private MemberEntity mockMemberA;
    private MemberEntity mockMemberB;
    private TeamEntity mockTeam;
    private LifecycleEntity mockMoum;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        mockPerformArticle = PerformArticleEntity.builder()
                .id(11)
                .moum(mockMoum)
                .team(mockTeam)
                .build();

        mockPerformArticleRequestDto = PerformArticleDto.Request.builder()
                .performanceName("공연게시글A")
                .teamId(1)
                .moumId(1)
                .membersId(List.of(1, 2))
                .build();

        mockPerformMemberA = PerformMember.builder()
                .id(1)
                .performanceArticle(mockPerformArticle)
                .member(mockMemberA)
                .build();

        mockPerformMemberB = PerformMember.builder()
                .id(2)
                .performanceArticle(mockPerformArticle)
                .member(mockMemberB)
                .build();

        mockMemberA = MemberEntity.builder()
                .id(1)
                .teams(new ArrayList<>())
                .records(new ArrayList<>())
                .username("멤버A")
                .build();

        mockMemberB = MemberEntity.builder()
                .id(2)
                .teams(new ArrayList<>())
                .records(new ArrayList<>())
                .username("멤버B")
                .build();

        mockMoum = LifecycleEntity.builder()
                .id(1)
                .build();

        mockTeam = TeamEntity.builder()
                .id(1)
                .build();

        mockPerformArticle = PerformArticleEntity.builder()
                .id(1)
                .performanceName("공연게시글A")
                .performMembers(List.of(mockPerformMemberA,mockPerformMemberB))
                .team(mockTeam)
                .moum(mockMoum)
                .build();


    }

    @Test
    @DisplayName("공연게시글 생성")
    @WithAuthUser
    void create_perform() throws Exception {
        // given
        //PerformArticleEntity performArticle = mockPerformArticleRequestDto.toEntity();
        PerformArticleDto.Response response = new PerformArticleDto.Response(mockPerformArticle);

        // then
        when(performArticleService.createPerformArticle(anyString(),any(),any())).thenReturn(response);

        // when
        MockMultipartFile file = new MockMultipartFile("file", "testfile.jpg", MediaType.IMAGE_JPEG_VALUE, "test file content".getBytes());

        MockMultipartFile mockMultipartFile = new MockMultipartFile("requestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(mockPerformArticleRequestDto).getBytes());

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/performs")
                        .file(file)
                        .file(mockMultipartFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.CREATE_PERFORM_ARTICLE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.performanceName").value("공연게시글A"));
    }

    @Test
    @DisplayName("공연게시글 단건 조회")
//    @Disabled(" 왜 갑자기 안되는건지.. ")
    @WithAuthUser
    void get_perform_by_id() throws Exception {
        // given
        int performArticleId = mockPerformArticle.getId();
        PerformArticleDto.Response response = new PerformArticleDto.Response(mockPerformArticle);

        // when
        when(performArticleService.getPerformArticleById(anyInt())).thenReturn(response);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/performs/{performArticleId}",performArticleId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data.performanceName").value("공연게시글A"));
    }

    @Test
    @DisplayName("공연게시글 리스트 조회")
//    @Disabled(" 왜 갑자기 안되는건지.. ")
    @WithAuthUser
    void get_perform_list() throws Exception {
        // given
        PerformArticleEntity mockPerformArticleB = PerformArticleEntity.builder()
                .id(1)
                .performanceName("공연게시글B")
                .performMembers(List.of(mockPerformMemberA,mockPerformMemberB))
                .build();

        List<PerformArticleDto.Response> responseList = List.of(
                new PerformArticleDto.Response(mockPerformArticle),
                new PerformArticleDto.Response(mockPerformArticleB)
        );

        // when
        when(performArticleService.getAllPerformArticle(anyInt(), anyInt())).thenReturn(responseList);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/performs-all")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.data[0].performanceName").value("공연게시글A"))
                .andExpect(jsonPath("$.data[1].performanceName").value("공연게시글B"));
    }

    @Test
    @DisplayName("이번 달 공연 게시글 리스트 조회 테스트")
    @WithAuthUser
    void getAllThisMonthPerformArticlesTest() throws Exception {
        // 이번 달 공연
        PerformArticleEntity mockArticleA = PerformArticleEntity.builder()
                .id(1)
                .performanceName("공연게시글A")
                .performanceStartDate(Date.from(LocalDateTime.now().withMonth(11).withDayOfMonth(15).atZone(ZoneId.systemDefault()).toInstant())) // 이번 달 날짜
                .performMembers(List.of(mockPerformMemberA, mockPerformMemberB))
                .build();

        PerformArticleEntity mockArticleB = PerformArticleEntity.builder()
                .id(2)
                .performanceName("공연게시글B")
                .performanceStartDate(Date.from(LocalDateTime.now().withMonth(11).withDayOfMonth(20).atZone(ZoneId.systemDefault()).toInstant())) // 이번 달 날짜
                .performMembers(List.of(mockPerformMemberA, mockPerformMemberB))
                .build();

        // 내년 공연 (조회 안되는 용도)
        PerformArticleEntity mockArticleC = PerformArticleEntity.builder()
                .id(3)
                .performanceName("공연게시글C")
                .performanceStartDate(Date.from(LocalDateTime.now().withYear(2025).withMonth(11).withDayOfMonth(15).atZone(ZoneId.systemDefault()).toInstant())) // 내년 날짜
                .performMembers(List.of(mockPerformMemberA, mockPerformMemberB))
                .build();

        PerformArticleEntity mockArticleD = PerformArticleEntity.builder()
                .id(4)
                .performanceName("공연게시글D")
                .performanceStartDate(Date.from(LocalDateTime.now().withYear(2025).withMonth(11).withDayOfMonth(20).atZone(ZoneId.systemDefault()).toInstant())) // 내년 날짜
                .performMembers(List.of(mockPerformMemberA, mockPerformMemberB))
                .build();

        List<PerformArticleDto.Response> responseList = List.of(
                new PerformArticleDto.Response(mockArticleA),
                new PerformArticleDto.Response(mockArticleB),
                new PerformArticleDto.Response(mockArticleC),
                new PerformArticleDto.Response(mockArticleD)
        );

        // when: 서비스에서 이번 달 공연만 반환하도록 설정
        when(performArticleService.getAllThisMonthPerformArticles(0, 10)).thenReturn((Page<PerformArticleDto.Response>) responseList);

        // then: 이번 달 공연 게시글만 조회되는지 확인
        mockMvc.perform(MockMvcRequestBuilders.get("/api/performs-all/this-month")
                        .param("page", "0")
                        .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.size()").value(2))
                .andExpect(jsonPath("$.data[0].title").value("This Month Performance 1"))
                .andExpect(jsonPath("$.data[1].title").value("This Month Performance 2"))
                .andExpect(jsonPath("$.data[0].title").doesNotExist())
                .andExpect(jsonPath("$.data[1].title").doesNotExist());
    }


    @Test
    @DisplayName("로그인 체크")
    void loginCheck_test(){
        // given
        String username = null;

        // when
        NeedLoginException exception = assertThrows(NeedLoginException.class, () -> {
            performArticleController.loginCheck(username);
        });

        // then
        assertThat(exception.getMessage()).isEqualTo("로그인을 해야합니다.");
    }



}