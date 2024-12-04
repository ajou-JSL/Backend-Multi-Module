package jsl.moum.community.article.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jsl.moum.community.article.domain.article_details.ArticleDetailsEntity;
import jsl.moum.community.article.dto.ArticleDetailsDto;
import jsl.moum.community.perform.dto.PerformArticleDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.article.dto.ArticleDto;
import jsl.moum.community.article.service.ArticleService;
import jsl.moum.custom.WithAuthUser;

import jsl.moum.global.response.ResponseCode;

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

import java.util.ArrayList;
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

    private ArticleEntity mockArticle;
    private ArticleEntity mockArticle2;
    private ArticleEntity mockArticle3;
    private MemberEntity mockAuthor;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        mockAuthor = MemberEntity.builder()
                .id(1)
                .teams(new ArrayList<>())
                .records(new ArrayList<>())
                .address("address")
                .username("mock author")
                .email("test email")
                .proficiency("중")
                .instrument("바이올린")
                .password("password")
                .role("ROLE_USER")
                .build();

        mockArticle = ArticleEntity.builder()
                .id(1)
                .author(mockAuthor)
                .title("title")
                .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                .build();

        mockArticle2 = ArticleEntity.builder()
                .id(2)
                .author(mockAuthor)
                .title("title2")
                .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                .build();
        mockArticle3 = ArticleEntity.builder()
                .id(3)
                .author(mockAuthor)
                .title("title3")
                .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                .build();

    }

    @Test
    @DisplayName("게시글 목록 조회 테스트")
    @WithAuthUser
    void getArticleList() throws Exception {
        // given
        List<ArticleDto.Response> responseList = List.of(
                new ArticleDto.Response(mockArticle),
                new ArticleDto.Response(mockArticle2),
                new ArticleDto.Response(mockArticle3)
        );

        // when
        when(articleService.getArticleList(0,10)).thenReturn(responseList);

        // then
        mockMvc.perform(get("/api/articles"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("title"))
                .andExpect(jsonPath("$.data[1].title").value("title2"))
                .andExpect(jsonPath("$.data[2].title").value("title3"));
    }

    @Test
    @DisplayName("카테고리별 게시글 목록 조회 테스트")
    @WithAuthUser
    void getArticlesByCategoryTest() throws Exception {
        ArticleEntity.ArticleCategories category = ArticleEntity.ArticleCategories.FREE_TALKING_BOARD;

        // given
        List<ArticleDto.Response> responseList = List.of(
                new ArticleDto.Response(mockArticle),
                new ArticleDto.Response(mockArticle2),
                new ArticleDto.Response(mockArticle3)
        );

        // when
        when(articleService.getArticlesByCategory(category,0,10)).thenReturn(responseList);

        // then
        // mockMvc.perform(get("/api/articles/category")
        //                .param("category", category.name()))
        mockMvc.perform(get("/api/articles/category?category=" + category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("title"))
                .andExpect(jsonPath("$.data[0].category").value("FREE_TALKING_BOARD"))
                .andExpect(jsonPath("$.data[1].title").value("title2"))
                .andExpect(jsonPath("$.data[1].category").value("FREE_TALKING_BOARD"));
    }


    @Test
    @DisplayName("게시글 상세 조회 테스트")
    @WithAuthUser
    void getArticleByIdTest() throws Exception {
        // given
        ArticleDetailsEntity mockArticleDetails = ArticleDetailsEntity.builder()
                .id(mockArticle.getId())
                .articleId(mockArticle.getId())
                .comments(new ArrayList<>())
                .build();

        ArticleDetailsDto.Response mockResponse = new ArticleDetailsDto.Response(mockArticleDetails, mockArticle);


        // when
        when(articleService.getArticleById(anyInt(),anyString())).thenReturn(mockResponse);

        // then
        mockMvc.perform(get("/api/articles/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.title").value("title"))
                .andExpect(jsonPath("$.data.author").value("mock author"))
                .andExpect(jsonPath("$.data.category").value("FREE_TALKING_BOARD"));

    }

    @Test
    @DisplayName("게시글 검색 테스트")
    @WithAuthUser
    void searchArticlesTest() throws Exception {
        String keyword = "searchKeyword";
        String category = "FREE_TALKING_BOARD";
        List<ArticleDto.Response> mockResponse = List.of(new ArticleDto.Response(mockArticle));

        //when(articleService.getArticleWithTitleSearch(keyword, category,0,10)).thenReturn(mockResponse);
        when(articleService.getArticleWithTitleSearch(anyString(), anyString(),anyInt(),anyInt())).thenReturn(mockResponse);

        mockMvc.perform(get("/api/articles/search?keyword=" + keyword + "&category=" + category))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data[0].title").value("title"));
    }

    @Test
    @DisplayName("게시글 작성 성공 테스트")
    @WithAuthUser
    void postArticle_Success() throws Exception {
        // given
        ArticleDto.Request articleRequest = ArticleDto.Request.builder()
                .id(1)
                .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                .title("title")
                .author(mockAuthor)
                .build();

        ArticleDto.Response response = new ArticleDto.Response(mockArticle);

        // when
        when(articleService.postArticle(any(), any(), anyString())).thenReturn(response);


        MockMultipartFile file = new MockMultipartFile("file", "testfile.jpg", MediaType.IMAGE_JPEG_VALUE, "test file content".getBytes());
        MockMultipartFile articleRequestDtoFile = new MockMultipartFile("articleRequestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(articleRequest).getBytes());

        // then
        mockMvc.perform(MockMvcRequestBuilders.multipart("/api/articles")
                        .file(file)
                        .file(articleRequestDtoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.ARTICLE_POST_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.title").value(articleRequest.getTitle()))
                .andExpect(jsonPath("$.data.author").value(articleRequest.getAuthor().getUsername()));
    }

}

