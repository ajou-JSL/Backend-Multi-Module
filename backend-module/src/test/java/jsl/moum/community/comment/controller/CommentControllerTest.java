package jsl.moum.community.comment.controller;



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
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.community.article.domain.article_details.ArticleDetailsEntity;
import jsl.moum.community.comment.domain.CommentEntity;
import jsl.moum.community.comment.dto.CommentDto;
import jsl.moum.community.comment.service.CommentService;
import jsl.moum.custom.WithAuthUser;
import jsl.moum.global.response.ResponseCode;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@WebMvcTest(CommentController.class)
class CommentControllerTest {

    @MockBean
    private CommentService commentService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MemberEntity author;
    private CommentEntity comment;
    private ArticleDetailsEntity articleDetails;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        author = MemberEntity.builder()
                .id(1)
                .email("test@gmail.com")
                .username("testuser")
                .password("12345123")
                .role("ROLE_USER")
                .build();

        articleDetails = ArticleDetailsEntity.builder()
                .id(1)
                .content("test content")
                .comments(new ArrayList<>())
                .build();

        comment = CommentEntity.builder()
                .id(1)
                .content("test content")
                .author(author)
                .articleDetails(articleDetails)
                .createdAt(LocalDateTime.now())
                .build();
    }
    @Test
    @DisplayName("댓글 작성 성공 테스트")
    @WithAuthUser()
    void create_comment_success() throws Exception {
        //given
        CommentDto.Request commentRequest = CommentDto.Request.builder()
                .author(author)
                .content("test content")
                .articleDetails(articleDetails)
                .build();

        CommentDto.Response response = new CommentDto.Response(comment);

        // when
        //when(commentService.createComment(commentRequest, author.getUsername(), articleDetails.getId())).thenReturn(response);
        when(commentService.createComment(any(), anyString(), anyInt())).thenReturn(response);


        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.COMMENT_CREATE_SUCCESS.getMessage()));
               // .andExpect(jsonPath("$.data.content").value(commentRequest.getContent()))
               // .andExpect(jsonPath("$.data.author").value(commentRequest.getAuthor().getUsername()));
    }

    @Test
    @DisplayName("댓글 수정 성공 테스트")
    @WithAuthUser()
    void update_comment_success() throws Exception {
        // given
        CommentDto.Request commentRequest = CommentDto.Request.builder()
                .author(author)
                .content("update content")
                .articleDetails(articleDetails)
                .build();

        CommentDto.Response response = new CommentDto.Response(comment);

        // when
        when(commentService.updateComment(any(), anyString(),anyInt())).thenReturn(response);


        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.COMMENT_UPDATE_SUCCESS.getMessage()));
        // .andExpect(jsonPath("$.data.content").value(commentRequest.getContent()))
        // .andExpect(jsonPath("$.data.author").value(commentRequest.getAuthor().getUsername()));
    }


    @Test
    @DisplayName("댓글 수정 실패 테스트 - 권한 에러")
    void update_comment_failed() throws Exception {
        // given
        CommentDto.Request commentRequest = CommentDto.Request.builder()
                .author(author)
                .content("update content")
                .articleDetails(articleDetails)
                .build();

        CommentDto.Response response = new CommentDto.Response(comment);

        // when
        when(commentService.updateComment(commentRequest, author.getUsername(), articleDetails.getId())).thenReturn(response);


        // then
        mockMvc.perform(MockMvcRequestBuilders.patch("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(commentRequest))
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("댓글 삭제 성공 테스트")
    @WithAuthUser(email = "test@user.com", username = "testuser")
    void delete_comment_success() throws Exception {
        // given
        CommentDto.Response response = new CommentDto.Response(comment);

        // when
        when(commentService.deleteComment(author.getUsername(), comment.getId())).thenReturn(response);


        // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(ResponseCode.COMMENT_DELETE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.commentId").value(comment.getId()))
                .andExpect(jsonPath("$.data.author").value(comment.getAuthor().getUsername()));

    }



    @Test
    @DisplayName("댓글 삭제 실페 테스트 - 권한 에러")
    void delete_comment_failed() throws Exception {
        // given
        CommentDto.Response response = new CommentDto.Response(comment);

        // when
        when(commentService.deleteComment(author.getUsername(), comment.getId())).thenReturn(response);


        // then
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/comments/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }



}

