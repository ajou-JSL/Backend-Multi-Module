package study.moum.community.article.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.multipart.MultipartFile;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.community.article.domain.article.ArticleEntity;
import study.moum.community.article.domain.article.ArticleRepository;
import study.moum.community.article.domain.article_details.ArticleDetailsEntity;
import study.moum.community.article.domain.article_details.ArticleDetailsRepository;
import study.moum.community.article.domain.article_details.ArticleRepositoryCustom;
import study.moum.community.article.dto.ArticleDetailsDto;
import study.moum.community.article.dto.ArticleDto;
import study.moum.objectstorage.StorageService;
import study.moum.global.error.exception.NeedLoginException;
import study.moum.global.error.exception.NoAuthorityException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ArticleServiceTest {

    @InjectMocks
    private ArticleService articleService;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private ArticleDetailsRepository articleDetailsRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private ArticleRepositoryCustom articleRepositoryCustom;

    private MemberEntity author;
    private ArticleEntity mockArticle;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Author 객체 생성
        author = MemberEntity.builder()
                .id(1)
                .email("test@gmail.com")
                .username("testAuthor")
                .password("12345123")
                .role("ROLE_ADMIN")
                .build();

        mockArticle = ArticleEntity.builder()
                .id(123123)
                .title("test title")
                .author(author)
                .build();
    }
    @Test
    @DisplayName("게시글 생성 테스트 - 로그인 사용자")
    void createArticleSuccess_LoggedInUser() throws IOException {
        // given: Article 생성
        ArticleDto.Request request = ArticleDto.Request.builder()
                .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                .author(author)
                .title("test title")
                .build();

        // Mock MultipartFile
        MultipartFile mockFile = Mockito.mock(MultipartFile.class);
        when(mockFile.getOriginalFilename()).thenReturn("testFile.jpg"); // 파일 확장자를 jpg로 변경
        when(mockFile.getContentType()).thenReturn("image/jpeg"); // 콘텐츠 타입을 image/jpeg로 변경
        when(mockFile.getInputStream()).thenReturn(new ByteArrayInputStream("test content".getBytes())); // 실제 이미지 콘텐츠로 변경 가능
        when(mockFile.getSize()).thenReturn((long) "test content".length()); // 사이즈는 이미지 콘텐츠에 맞게 변경 가능

        // Mock 동작
        when(memberRepository.findByUsername(author.getUsername())).thenReturn(author); // db에 있는 유저


        when(articleRepository.findById(anyInt())).thenReturn(Optional.of(mockArticle));
//        when(articleRepository.save(any(ArticleEntity.class))).thenAnswer(invocation -> {
//            ArticleEntity article = invocation.getArgument(0);
//            article.setId(1); // 가상의 id
//            return article;
//        });
        //when(articleDetailsRepository.save(any(ArticleDetailsEntity.class))).thenReturn(new ArticleDetailsEntity());

        // when
        ArticleDto.Response actualResponse = articleService.postArticle(request, mockFile, author.getUsername());

        // then
        assertEquals("test title", actualResponse.getTitle());
        assertEquals(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD, actualResponse.getCategory());
    }



    @Test
    @DisplayName("게시글 생성 테스트 - 로그인하지 않은 사용자")
    void createArticleFail_NeedLoginException() {
        // given : Article 생성
        ArticleDto.Request request = ArticleDto.Request.builder()
                .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                .author(author)
                .title("test title")
                .build();

        // Mock 동작 : db에 없는 username
        when(memberRepository.findByUsername("not_member_user")).thenReturn(null);

        // when & then
        assertThrows(NeedLoginException.class, () -> {
            articleService.postArticle(request, any(),"로그인을 해야합니다.");
        });
    }

    @Test
    @DisplayName("게시글 조회 테스트") // 해당 메서드에서 article, articleDetails 둘 다 조회하고있는거 생각
    void getArticleById() {
        // given: Article Entity, ArticleDetails Entity 생성
        ArticleEntity article = ArticleEntity.builder()
                .id(1)
                .title("test title")
                .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                .author(author)
                .build();

        ArticleDetailsEntity articleDetails = ArticleDetailsEntity.builder()
                .id(1)
                .content("test content")
                .comments(new ArrayList<>())
                .articleId(article.getId())
                .fileUrl("file url")
                .build();

        // Mock 동작
        when(articleRepository.findById(1)).thenReturn(Optional.of(article));
        when(articleDetailsRepository.findById(1)).thenReturn(Optional.of(articleDetails));

        // when
        ArticleDetailsDto.Response response = articleService.getArticleById(1);

        // then
        assertNotNull(response);
        assertEquals(1, response.getId());
        assertEquals("test title", response.getTitle());
        assertEquals("test content", response.getContent());
        assertEquals("file url", response.getFileUrl());
    }

    @Test
    @DisplayName("게시글 목록 조회 테스트")
    void getArticleList() {
        // given : 게시글 리스트 생성
        List<ArticleEntity> mockArticle = List.of(
                ArticleEntity.builder()
                        .id(1)
                        .title("test title 1")
                        .author(author)
                        .build(),
                ArticleEntity.builder()
                        .id(2)
                        .title("test title 2")
                        .author(author)
                        .build()
        );

        // Mock 동작 (페이지 요청에 대한 Mock)
        when(articleRepository.findAll(PageRequest.of(0, 10))).thenReturn(new PageImpl<>(mockArticle));

        // when
        List<ArticleDto.Response> responseList = articleService.getArticleList(0, 10);

        // then
        assertEquals(2, responseList.size()); // list 크기
        assertEquals("test title 1", responseList.get(0).getTitle());
        assertEquals("test title 2", responseList.get(1).getTitle());
    }

    @Test
    @DisplayName("게시글 수정 성공")
    void updateArticleWithoutAuthorization() throws IOException {
        // given : Article, Article Details 생성
        ArticleEntity article = ArticleEntity.builder()
                .id(1)
                .title("title")
                .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                .author(author)
                .build();

        ArticleDetailsEntity articleDetails = ArticleDetailsEntity.builder()
                .id(1)
                .content("content")
                .comments(new ArrayList<>())
                .articleId(article.getId())
                .build();

        // given : update request dto 생성, details에서 title 수정 가능해야함
        ArticleDetailsDto.Request updateArticleDetailsRequest = ArticleDetailsDto.Request.builder()
                .id(1)
                .title("updated title")
                .content("updated content")
                .category(ArticleEntity.ArticleCategories.RECRUIT_BOARD)
                .build();

        // Mock 동작 : repository 에서 탐색
        when(articleRepository.findById(1)).thenReturn(Optional.of(article));
        when(articleDetailsRepository.findById(1)).thenReturn(Optional.of(articleDetails));

        // when : 게시글 수정 요청
        ArticleDetailsDto.Response response = articleService.updateArticleDetails(1, updateArticleDetailsRequest, any(),author.getUsername());

        // then
        assertNotNull(response);
        assertEquals("updated title", response.getTitle());
        assertEquals("updated title", article.getTitle()); // details에서 수정한게 article에 잘 들어가는지
        assertEquals("updated content", response.getContent());
        assertEquals("RECRUIT_BOARD", response.getCategory());

        assertThrows(NoAuthorityException.class, () -> {
            articleService.updateArticleDetails(1, updateArticleDetailsRequest, any(),"not_author_user");
        });
    }



    @Test
    @DisplayName("게시글 삭제 테스트") // article, details 둘 다 삭제되는지 봐야함
    void deleteArticle() {
        // given : Article, Article Details 생성
        ArticleEntity article = ArticleEntity.builder()
                .id(1)
                .title("title")
                .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                .author(author)
                .build();

        ArticleDetailsEntity articleDetails = ArticleDetailsEntity.builder()
                .id(1)
                .content("content")
                .comments(new ArrayList<>())
                .articleId(article.getId())
                .build();

        // Mock 동작 : repository 에서 탐색
        when(articleRepository.findById(1)).thenReturn(Optional.of(article));
        when(articleDetailsRepository.findById(1)).thenReturn(Optional.of(articleDetails));

        // when
        ArticleDto.Response response = articleService.deleteArticleDetails(1, author.getUsername());

        // then
        assertEquals("title", response.getTitle());

        verify(articleRepository).deleteById(1);
        verify(articleDetailsRepository).deleteById(1);

        assertThrows(NoAuthorityException.class, () -> {
            articleService.deleteArticleDetails(1, "not_author_user");
        });
    }

    @Test
    @DisplayName("카테고리에 따른 게시글 목록 조회 테스트")
    void getArticlesByCategory() {
        // given : 게시글 리스트 생성
        List<ArticleEntity> mockArticle = List.of(
                ArticleEntity.builder()
                        .id(1)
                        .title("test title 1")
                        .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                        .author(author)
                        .build(),
                ArticleEntity.builder()
                        .id(2)
                        .title("test title 2")
                        .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                        .author(author)
                        .build(),
                ArticleEntity.builder()
                        .id(3)
                        .title("test title 3")
                        .category(ArticleEntity.ArticleCategories.RECRUIT_BOARD)
                        .author(author)
                        .build(),
                ArticleEntity.builder()
                        .id(4)
                        .title("test title 4")
                        .category(ArticleEntity.ArticleCategories.RECRUIT_BOARD)
                        .author(author)
                        .build()
        );

        // Mock 동작
        when(articleRepositoryCustom.findFreeTalkingArticles(0,10)).thenReturn(mockArticle); // 자유게시판
        when(articleRepositoryCustom.findRecruitingdArticles(0,10)).thenReturn(mockArticle); // 모집게시판

        // when
        List<ArticleDto.Response> Freearticles = articleService.getArticlesByCategory(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD,0,10);
        List<ArticleDto.Response> Recruitarticles = articleService.getArticlesByCategory(ArticleEntity.ArticleCategories.RECRUIT_BOARD,0,10);

        // then
        assertEquals(4, mockArticle.size());
        assertEquals("test title 1", Freearticles.get(0).getTitle());
        assertEquals(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD, Freearticles.get(0).getCategory());
        assertEquals("test title 3", Recruitarticles.get(2).getTitle());
        assertEquals(ArticleEntity.ArticleCategories.RECRUIT_BOARD, Recruitarticles.get(2).getCategory());
    }

    @Test
    @DisplayName("게시글 제목으로 검색 테스트")
    void getArticleWithTitleSearch() {
        // given : 게시글 리스트 생성
        List<ArticleEntity> mockArticle = List.of(
                ArticleEntity.builder()
                        .id(1)
                        .title("test title 1")
                        .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                        .author(author)
                        .build(),
                ArticleEntity.builder()
                        .id(2)
                        .title("test title 2")
                        .category(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                        .author(author)
                        .build(),
                ArticleEntity.builder()
                        .id(3)
                        .title("test title 3")
                        .category(ArticleEntity.ArticleCategories.RECRUIT_BOARD)
                        .author(author)
                        .build(),
                ArticleEntity.builder()
                        .id(4)
                        .title("test title 4")
                        .category(ArticleEntity.ArticleCategories.RECRUIT_BOARD)
                        .author(author)
                        .build()
        );

        // given : 검색어 설정
        String keyword = "test";


        /*
            todo : 이거 삽질했는데 왜 되는지 모름. 나중에 자세히 다시 보기
         */
        // Mock 동작
        List<ArticleEntity> articleList = mockArticle.stream()
                .filter(article -> article.getTitle().contains(keyword) &&
                        article.getCategory() == ArticleEntity.ArticleCategories.FREE_TALKING_BOARD)
                .collect(Collectors.toList());

        when(articleRepositoryCustom.searchArticlesByTitleKeyword(keyword, "FREE_TALKING_BOARD",0,10))
                .thenReturn(articleList);

        // when
        List<ArticleDto.Response> response = articleService.getArticleWithTitleSearch(keyword, "FREE_TALKING_BOARD",0,10);

        // then
        assertEquals(2, response.size());
        assertEquals(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD,response.get(0).getCategory());
        assertEquals(ArticleEntity.ArticleCategories.FREE_TALKING_BOARD,response.get(1).getCategory());
    }

}
