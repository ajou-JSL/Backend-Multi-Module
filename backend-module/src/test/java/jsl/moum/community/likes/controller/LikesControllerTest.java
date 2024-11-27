package jsl.moum.community.likes.controller;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.likes.domain.LikesEntity;
import jsl.moum.community.likes.dto.LikesDto;
import jsl.moum.community.likes.service.LikesService;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.custom.WithAuthUser;
import jsl.moum.global.response.ResponseCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(LikesController.class)
public class LikesControllerTest {

    @MockBean
    private LikesService likesService;

    @MockBean
    private MemberRepository memberRepository;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MemberEntity mockAuthor;
    private MemberEntity mockMember;
    private ArticleEntity mockArticle;
    private LikesEntity mockLikes;
    private PerformArticleEntity mockPerformArticle;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        mockAuthor = MemberEntity.builder()
                .id(1)
                .username("author")
                .build();

        mockMember = MemberEntity.builder()
                .id(2)
                .username("member")
                .build();

        mockArticle = ArticleEntity.builder()
                .id(1)
                .title("title")
                .build();

        mockPerformArticle = PerformArticleEntity.builder()
                .id(1)
                .performanceName("perform name")
                .build();

        mockLikes = LikesEntity.builder()
                .id(1)
                .member(mockMember)
                .article(mockArticle)
                .performArticle(mockPerformArticle)
                .build();
    }

    @Test
    @DisplayName("게시글 좋아요 생성 성공")
    @WithAuthUser
    void createArticleLikes_success() throws Exception {
        // given
        LikesDto.Response likesResponse = new LikesDto.Response(mockLikes);
        Mockito.when(likesService.createLikes(anyString(), anyInt())).thenReturn(likesResponse);
        Mockito.when(memberRepository.findByUsername(anyString())).thenReturn(mockAuthor);

        // when & then
        mockMvc.perform(post("/api/articles/likes/{articleId}", 1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ResponseCode.LIKES_CREATE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.data.memberId").value(2))
                .andExpect(jsonPath("$.data.articleId").value(1));
    }

    @Test
    @DisplayName("게시글 좋아요 삭제 성공")
    @WithAuthUser
    void deleteArticleLikes_success() throws Exception {
        // given
        LikesDto.Response likesResponse = new LikesDto.Response(mockLikes);
        Mockito.when(likesService.deleteLikes(anyString(), anyInt())).thenReturn(likesResponse);
        Mockito.when(memberRepository.findByUsername(anyString())).thenReturn(mockAuthor);

        // when & then
        mockMvc.perform(delete("/api/articles/likes/{articleId}", 1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ResponseCode.LIKES_DELETE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.data.memberId").value(2))
                .andExpect(jsonPath("$.data.articleId").value(1));
    }

    @Test
    @DisplayName("공연 게시글 좋아요 생성 성공")
    @WithAuthUser
    void createPerformLikes_success() throws Exception {
        // given
        LikesDto.Response likesResponse = new LikesDto.Response(mockLikes);
        Mockito.when(likesService.createPerformLikes(anyString(), anyInt())).thenReturn(likesResponse);
        Mockito.when(memberRepository.findByUsername(anyString())).thenReturn(mockAuthor);

        // when & then
        mockMvc.perform(post("/api/performs/likes/{performArticleId}", 1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ResponseCode.LIKES_CREATE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.data.memberId").value(2))
                .andExpect(jsonPath("$.data.performArticleId").value(1));
    }

    @Test
    @DisplayName("공연 게시글 좋아요 삭제 성공")
    @WithAuthUser
    void deletePerformLikes_success() throws Exception {
        // given
        LikesDto.Response likesResponse = new LikesDto.Response(mockLikes);
        Mockito.when(likesService.deletePerformLikes(anyString(), anyInt())).thenReturn(likesResponse);
        Mockito.when(memberRepository.findByUsername(anyString())).thenReturn(mockAuthor);

        // when & then
        mockMvc.perform(delete("/api/performs/likes/{performArticleId}", 1)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(ResponseCode.LIKES_DELETE_SUCCESS.getStatus()))
                .andExpect(jsonPath("$.data.memberId").value(2))
                .andExpect(jsonPath("$.data.performArticleId").value(1));
    }
}
