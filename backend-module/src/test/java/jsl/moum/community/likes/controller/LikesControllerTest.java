package jsl.moum.community.likes.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.likes.service.LikesService;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

public class LikesControllerTest {

    @InjectMocks
    private LikesController likesController;

    @Mock
    private LikesService likesService;

    private MemberEntity member;
    private ArticleEntity article;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(likesController)
                .alwaysDo(print())
                .build();
        objectMapper = new ObjectMapper();

        member = MemberEntity.builder()
                .id(1)
                .role("ROLE_USER")
                .username("testuser")
                .password("1234")
                .email("test@user.com")
                .build();

        article = ArticleEntity.builder()
                .id(1)
                .category(ArticleEntity.ArticleCategories.RECRUIT_BOARD)
                .title("test title")
                .author(member)
                .likesCount(0)
                .build();
    }
//
//    @Test
//    @DisplayName("게시글 좋아요 생성 성공")
//    void article_likes_success() throws Exception{
//        // given
//        MemberEntity anothor_member = MemberEntity.builder()
//                .id(333)
//                .role("ROLE_USER")
//                .username("anothor_user")
//                .password("1234")
//                .email("another@user.com")
//                .build();
//
//        LikesEntity likesEntity = LikesEntity.builder()
//                .id(999)
//                .article(article)
//                .member(anothor_member)
//                .build();
//
//        LikesDto.Request likesRequestDto = LikesDto.Request.builder()
//                .member(anothor_member)
//                .article(article)
//                .build();
//
//        LikesDto.Response likesResponseDto = new LikesDto.Response(999,333,1);
//
//        // when
//        when(likesService.createLikes(anothor_member.getUsername(), article.getId())).thenReturn(likesResponseDto);
//        when(likesService.findMember(anothor_member.getUsername())).thenReturn(anothor_member);
//        when(likesService.findArticle(article.getId())).thenReturn(article);
//
//
//        // then
//        mockMvc.perform(post("/api/likes/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(likesRequestDto))
//                        .with(csrf())) // CSRF 토큰 추가
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.message").value(ResponseCode.LIKES_CREATE_SUCCESS));
//
//    }

//    @Test
//    @DisplayName("게시글 좋아요 실패 - 이미 좋아요 누름")
//    @WithAuthUser
//    void article_likes_fail_already_liked() throws Exception {
//        // given
//        LikesDto.Request likesRequestDto = LikesDto.Request.builder()
//                .member(member)
//                .article(article)
//                .build();
//
//        // 이미 좋아요를 누른 상태를 모의합니다.
//        doThrow(new CustomException(ErrorCode.DUPLICATE_LIKES))
//                .when(likesService).createLikes(member.getUsername(), article.getId());
//
//        // when
//        // then
//        mockMvc.perform(post("/api/likes/1")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(likesRequestDto))
//                        .with(csrf())) // CSRF 토큰 추가
//                .andExpect(status().isConflict()) // 409 Conflict 상태 코드 확인
//                .andExpect(jsonPath("$.data.message").value("이미 좋아요를 누른 상태입니다."));
//    }

    @Test
    @DisplayName("게시글 좋아요 실패 - 삭제된 게시글")
    void article_likes_fail_deleted_article(){
        // given

        // when

        // then

    }

    @Test
    @DisplayName("게시글 좋아요 삭제 성공")
    void delete_likes_success(){
        // given

        // when

        // then

    }

    @Test
    @DisplayName("게시글 좋아요 삭제 실패 - 권한 없음")
    void delete_likes_fail_NoAuthority(){
        // given

        // when

        // then

    }
}
