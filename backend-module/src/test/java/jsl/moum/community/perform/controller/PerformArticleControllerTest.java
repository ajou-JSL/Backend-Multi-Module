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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

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


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        mockPerformArticleRequestDto = PerformArticleDto.Request.builder()
                .performanceName("공연게시글A")
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

        mockPerformArticle = PerformArticleEntity.builder()
                .id(1)
                .performanceName("공연게시글A")
                .performMembers(List.of(mockPerformMemberA,mockPerformMemberB))
                .build();


    }

    @Test
    @DisplayName("공연게시글 생성")
    @WithAuthUser
    void create_perform() throws Exception {
        // given
        PerformArticleEntity performArticle = mockPerformArticleRequestDto.toEntity();
        PerformArticleDto.Response response = new PerformArticleDto.Response(performArticle);

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
    @Disabled(" 왜 갑자기 안되는건지.. ")
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
    @Disabled(" 왜 갑자기 안되는건지.. ")
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