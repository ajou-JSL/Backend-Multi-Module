package study.moum.community.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
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
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.community.article.domain.article.ArticleEntity;
import study.moum.community.article.dto.ArticleDetailsDto;
import study.moum.community.article.dto.ArticleDto;
import study.moum.community.article.service.ArticleService;
import study.moum.custom.WithAuthUser;

import study.moum.global.response.ResponseCode;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

@WebMvcTest(ArticleController.class)
class ArticleControllerTest {

    @MockBean
    private ArticleService articleService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

    @Test
    @DisplayName("게시글 목록 조회 테스트")
    @WithAuthUser
    void getArticleList() throws Exception {
        // given : 게시글 리스트 생성
        List<ArticleDto.Response> mockResponse = List.of(
                new ArticleDto.Response(1, "Title 1", ArticleEntity.ArticleCategories.FREE_TALKING_BOARD, 10, 5, 3, "author1"),
                new ArticleDto.Response(2, "Title 2", ArticleEntity.ArticleCategories.FREE_TALKING_BOARD, 15, 3, 4, "author2"),
                new ArticleDto.Response(3, "Title 3", ArticleEntity.ArticleCategories.FREE_TALKING_BOARD, 20, 2, 1, "author3")
        );

        // when
        when(articleService.getArticleList(0,10)).thenReturn(mockResponse);

        // then
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Title 1"))
                .andExpect(jsonPath("$.data[1].title").value("Title 2"))
                .andExpect(jsonPath("$.data[2].title").value("Title 3"));
    }

    @Test
    @DisplayName("카테고리별 게시글 목록 조회 테스트")
    @WithAuthUser
    void getArticlesByCategoryTest() throws Exception {
        ArticleEntity.ArticleCategories category = ArticleEntity.ArticleCategories.FREE_TALKING_BOARD;

        // given: 게시글 리스트 생성
        List<ArticleDto.Response> mockResponse = List.of(
                new ArticleDto.Response(1, "Title 1", ArticleEntity.ArticleCategories.FREE_TALKING_BOARD, 10, 5, 3, "author1"),
                new ArticleDto.Response(2, "Title 2", ArticleEntity.ArticleCategories.FREE_TALKING_BOARD, 15, 3, 4, "author2"),
                new ArticleDto.Response(3, "Title 3", ArticleEntity.ArticleCategories.RECRUIT_BOARD, 20, 2, 1, "author3")
        );

        // when: 서비스의 메서드 호출 시 mockResponse를 반환하도록 설정
        when(articleService.getArticlesByCategory(category,0,10)).thenReturn(mockResponse);

        // then: 카테고리별 게시글 목록 조회
        // mockMvc.perform(get("/api/articles/category")
        //                .param("category", category.name()))  // 카테고리 값을 파라미터로 추가
        mockMvc.perform(get("/api/articles/category?category=" + category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Title 1"))
                .andExpect(jsonPath("$.data[0].category").value("FREE_TALKING_BOARD"))
                .andExpect(jsonPath("$.data[1].title").value("Title 2"))
                .andExpect(jsonPath("$.data[1].category").value("FREE_TALKING_BOARD"));
    }

//
//    @Test
//    @DisplayName("게시글 상세 조회 테스트")
//    @WithAuthUser
//    void getArticleByIdTest() throws Exception {
//        // given : Author 생성
//        MemberEntity author = MemberEntity.builder()
//                .id(1)
//                .email("test@gmail.com")
//                .username("testAuthor")
//                .password("12345123")
//                .role("ROLE_ADMIN")
//                .build();
//
//        // given : Article 생성
//        ArticleDetailsDto.Response mockResponse = new ArticleDetailsDto.Response(
//                1, "Title", "FREE_TALKING_BOARD", "Content", 10, 2, 3, author.getUsername(), List.of()
//        );
//
//        // when
//        when(articleService.getArticleById(1)).thenReturn(mockResponse);
//
//        // then
//        mockMvc.perform(get("/api/articles/1"))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.data.title").value("Title"))
//                .andExpect(jsonPath("$.data.author").value("testAuthor"))
//                .andExpect(jsonPath("$.data.category").value("FREE_TALKING_BOARD"))
//                .andExpect(jsonPath("$.data.content").value("Content"));
//
//    }

    @Test
    @DisplayName("게시글 검색 테스트")
    @WithAuthUser
    void searchArticlesTest() throws Exception {
        String keyword = "searchKeyword";
        String category = "FREE_TALKING_BOARD";
        List<ArticleDto.Response> mockResponse = List.of(new ArticleDto.Response(1, "Title", ArticleEntity.ArticleCategories.FREE_TALKING_BOARD, 10, 5, 3, "author"));

        when(articleService.getArticleWithTitleSearch(keyword, category,0,10)).thenReturn(mockResponse);

        mockMvc.perform(get("/api/articles/search?keyword=" + keyword + "&category=" + category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("Title"));
    }

    @Test
    @DisplayName("게시글 작성 성공 테스트")
    @WithAuthUser(email = "test@user.com", username = "testuser")
    void postArticle_Success() throws Exception {
        // given : Author 생성
        MemberEntity author = MemberEntity.builder()
                .id(1)
                .email("test@gmail.com")
                .username("testuser")
                .password("12345123")
                .role("ROLE_ADMIN")
                .build();

        // given : Article 생성
        ArticleDto.Request articleRequest = ArticleDto.Request.builder()
                .id(1)
                .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                .title("test title")
                .author(author)
                .build();

        ArticleDto.Response response = new ArticleDto.Response(1, "test title", ArticleEntity.ArticleCategories.FREE_TALKING_BOARD, 0, 0, 0, "testuser");

        // when
        when(articleService.postArticle(any(), any(), anyString())).thenReturn(response);

        // MockMultipartFile 생성
        MockMultipartFile file = new MockMultipartFile("file", "testfile.jpg", MediaType.IMAGE_JPEG_VALUE, "test file content".getBytes());

        // ArticleRequestDto를 JSON으로 변환하여 MockMultipartFile로 생성
        MockMultipartFile articleRequestDtoFile = new MockMultipartFile("articleRequestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(articleRequest).getBytes());

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/articles")
                        .file(file) // 파일 추가
                        .file(articleRequestDtoFile) // ArticleRequestDto 추가
                        .contentType(MediaType.MULTIPART_FORM_DATA) // Content-Type 설정
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.ARTICLE_POST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.title").value(articleRequest.getTitle()))
                .andExpect(jsonPath("$.data.author").value(articleRequest.getAuthor().getUsername()));
    }

}

