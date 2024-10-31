package study.moum.community.comment.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.test.context.support.WithMockUser;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.community.article.domain.article.ArticleEntity;
import study.moum.community.article.domain.article.ArticleRepository;
import study.moum.community.article.domain.article_details.ArticleDetailsEntity;
import study.moum.community.article.domain.article_details.ArticleDetailsRepository;
import study.moum.community.comment.domain.CommentEntity;
import study.moum.community.comment.domain.CommentRepository;
import study.moum.community.comment.dto.CommentDto;
import study.moum.custom.WithAuthUser;
import study.moum.global.error.ErrorCode;
import study.moum.global.error.exception.CustomException;
import study.moum.global.error.exception.MemberNotExistException;
import study.moum.global.error.exception.NoAuthorityException;

import java.util.ArrayList;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CommentServiceTest {

    @InjectMocks
    private CommentService commentService;

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private ArticleDetailsRepository articleDetailsRepository;

    @Mock
    private MemberRepository memberRepository;

    private MemberEntity mockAuthor;
    private ArticleDetailsEntity mockArticleDetails;
    private ArticleEntity mockArticle;
    private CommentEntity mockComment;


    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        // 테스트에 필요한 객체들 초기화
        mockAuthor = MemberEntity.builder()
                .id(1)
                .email("test@user.com")
                .password("1234")
                .username("testuser")
                .role("ROLE_ADMIN")
                .build();

        mockArticle = ArticleEntity.builder()
                .id(1)
                .title("test title")
                .author(mockAuthor)
                .category(ArticleEntity.ArticleCategories.RECRUIT_BOARD)
                .commentCount(0)
                .build();

        mockArticleDetails = ArticleDetailsEntity.builder()
                .id(1)
                .comments(new ArrayList<>())
                .content("test content")
                .articleId(mockArticle.getId())
                .build();

        mockComment = CommentEntity.builder()
                .id(1)
                .articleDetails(mockArticleDetails)
                .author(mockAuthor)
                .content("test content")
                .build();
    }

    @Test
    @DisplayName("댓글 생성 성공")
    @WithAuthUser(email = "test@user.com", username = "testuser")
    void create_comment_success(){
        // given
        CommentDto.Request commentRequest = CommentDto.Request.builder()
                .articleDetails(mockArticleDetails)
                .content("test content")
                .author(mockAuthor)
                .build();

        // Mock 동작
        when(memberRepository.findByUsername(mockAuthor.getUsername())).thenReturn(mockAuthor);
        when(articleDetailsRepository.findById(mockArticleDetails.getId())).thenReturn(Optional.ofNullable(mockArticleDetails));
        when(articleRepository.findById(mockArticle.getId())).thenReturn(Optional.ofNullable(mockArticle));

        // when
        CommentDto.Response response = commentService.createComment(commentRequest, mockAuthor.getUsername(), mockArticle.getId());

        // then
        verify(commentRepository).save(any(CommentEntity.class));
        assertEquals("test content", response.getContent());
        assertEquals(1, mockArticle.getCommentCount());

    }
    @Test
    @DisplayName("댓글 생성 실패 - 없는 사용자")
    @WithMockUser(username = "testuser") // 로그인한 사용자 설정
    void createComment_Fail_MemberNotFound() {
        // Given
        when(memberRepository.findByUsername("testuser")).thenReturn(null); // 사용자를 찾지 못함
        when(articleRepository.findById(1)).thenReturn(Optional.of(mockArticle)); // Mock Article 설정

        CommentDto.Request commentRequestDto = CommentDto.Request.builder()
                .content("test comment")
                .build();

        // When & Then
        MemberNotExistException exception = assertThrows(MemberNotExistException.class,
                () -> commentService.createComment(commentRequestDto, "testuser", 1)); // 예외 발생 확인

        assertEquals(ErrorCode.MEMBER_NOT_EXIST, exception.getErrorCode()); // 예외 코드 검증
    }

    @Test
    @DisplayName("댓글 수정 성공")
    @WithAuthUser(email = "test@user.com", username = "testuser")
    void update_comment_success(){
        // given
        CommentDto.Request updateRequest = CommentDto.Request.builder()
                .articleDetails(mockArticleDetails)
                .content("update content")
                .author(mockAuthor)
                .build();

        // Mock 동작
        when(memberRepository.findByUsername(mockAuthor.getUsername())).thenReturn(mockAuthor);
        when(articleDetailsRepository.findById(mockArticleDetails.getId())).thenReturn(Optional.ofNullable(mockArticleDetails));
        when(articleRepository.findById(mockArticle.getId())).thenReturn(Optional.ofNullable(mockArticle));
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.ofNullable(mockComment));

        // when
        CommentDto.Response response = commentService.updateComment(updateRequest, mockAuthor.getUsername(), mockArticle.getId());

        // then
        verify(commentRepository).save(any(CommentEntity.class));
        assertEquals("update content", response.getContent());
    }

    @Test
    @DisplayName("댓글 삭제 성공")
    @WithAuthUser(email = "test@user.com", username = "testuser")
    void delete_comment_success(){
        // given

        // Mock 동작
        when(memberRepository.findByUsername(mockAuthor.getUsername())).thenReturn(mockAuthor);
        when(articleDetailsRepository.findById(mockArticleDetails.getId())).thenReturn(Optional.ofNullable(mockArticleDetails));
        when(articleRepository.findById(mockArticle.getId())).thenReturn(Optional.ofNullable(mockArticle));
        when(commentRepository.findById(mockComment.getId())).thenReturn(Optional.ofNullable(mockComment));

        // when
        CommentDto.Response response = commentService.deleteComment(mockAuthor.getUsername(), mockArticle.getId());

        // then
        verify(commentRepository).deleteById(mockComment.getId());
        assertEquals(1, response.getCommentId());
        assertEquals(0, mockArticle.getCommentCount());
    }

    @Test
    @DisplayName("댓글 수정 실패 - 없는 댓글")
    @WithMockUser(username = "testuser") // 로그인한 사용자 설정
    void updateComment_Fail_NoComment() {
        // Given
        CommentDto.Request requestDto = new CommentDto.Request("수정된 댓글 내용", mockAuthor, mockArticleDetails);

        when(commentRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> commentService.updateComment(requestDto, "testuser", 1));
        assertEquals(ErrorCode.COMMENT_NOT_FOUND, exception.getErrorCode());
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 권한 없음")
    void deleteComment_Fail_NoAuthorization() {
        // Given
        when(memberRepository.findByUsername("testuser")).thenReturn(mockAuthor);
        when(commentRepository.findById(1)).thenReturn(Optional.of(mockComment));

        // 다른 사용자가 작성한 댓글로 설정
        MemberEntity anotherUser = MemberEntity.builder()
                .id(1)
                .email("another@user.com")
                .password("1234")
                .username("anotherUser")
                .role("ROLE_ADMIN")
                .build();

        mockComment.setAuthor(anotherUser);

        // When & Then
        NoAuthorityException exception = assertThrows(NoAuthorityException.class,
                () -> commentService.deleteComment("testuser", 1));
        assertNotNull(exception);
    }

    @Test
    @DisplayName("댓글 삭제 실패 - 이미 삭제된 댓글")
    @WithMockUser(username = "testuser") // 로그인한 사용자 설정
    void deleteComment_Fail_AlreadyDeleted() {
        // Given
        when(commentRepository.findById(1)).thenReturn(Optional.empty());

        // When & Then
        CustomException exception = assertThrows(CustomException.class,
                () -> commentService.deleteComment("testuser", 1));
        assertEquals(ErrorCode.COMMENT_ALREADY_DELETED, exception.getErrorCode());
    }
}
