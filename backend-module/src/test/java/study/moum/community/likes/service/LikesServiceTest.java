package study.moum.community.likes.service;

import jsl.moum.community.likes.service.LikesService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.community.article.domain.article.ArticleEntity;
import jsl.moum.community.article.domain.article.ArticleRepository;
import jsl.moum.community.likes.domain.LikesEntity;
import jsl.moum.community.likes.domain.LikesRepository;
import jsl.moum.community.likes.dto.LikesDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static reactor.core.publisher.Mono.when;

public class LikesServiceTest {

    @InjectMocks
    private LikesService likesService;

    @Mock
    private LikesRepository likesRepository;

    @Mock
    private ArticleRepository articleRepository;

    @Mock
    private MemberRepository memberRepository;

    private MemberEntity member;
    private ArticleEntity article;
    private LikesEntity likes;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // Mock Member
        member = MemberEntity.builder()
                .id(3)
                .username("testuser")
                .build();

        // Mock Article
        article = ArticleEntity.builder()
                .id(6)
                .likesCount(0)
                .author(member)
                .build();

        // Mock LikesEntity
        likes = LikesEntity.builder()
                .id(1)
                .article(article)
                .member(member)
                .build();
    }

    @Test
    @DisplayName("좋아요 등록 성공")
    void create_likes_success() {
        // given
        String memberName = "anothor_member";
        int articleId = article.getId();

        // repository 기능 mocking
        given(memberRepository.findByUsername(memberName)).willReturn(member);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        given(likesRepository.save(any(LikesEntity.class))).willReturn(likes);

        // 자기 게시글에 누르는게 아닐 때 테스트 필요

        LikesDto.Response response = likesService.createLikes(memberName, articleId);

        // then
        verify(likesRepository).save(any(LikesEntity.class));
        verify(articleRepository).save(article);
        assertEquals(3, response.getMemberId());
        assertEquals(6, response.getArticleId());
        assertEquals(1,article.getLikesCount());
    }

    @Test
    @DisplayName("좋아요 등록 실패 - 본인의 게시글")
    void create_likes_fail_self_likes(){
        // given
        String memberName = member.getUsername(); // 좋아요를 등록하려는 사용자
        int articleId = article.getId(); // 사용자의 게시글 ID

        // Mocking the behavior of repositories
        given(memberRepository.findByUsername(memberName)).willReturn(member);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article)); // 게시글 존재


        // likesRepository.save()가 호출되지 않았는지 확인
        verify(likesRepository, never()).save(any(LikesEntity.class));
        assertEquals("자신의 게시글에는 좋아요를 누를 수 없습니다.",ErrorCode.CANNOT_CREATE_SELF_LIKES.getMessage());
    }

    @Test
    @DisplayName("좋아요 등록 실패 - 없는 게시글")
    void create_likes_fail_no_article() {
        // given
        String memberName = member.getUsername();
        int articleId = 999; // 존재하지 않는 게시글 ID

        // Mocking the behavior of repositories
        given(memberRepository.findByUsername(memberName)).willReturn(member);
        given(articleRepository.findById(articleId)).willReturn(Optional.empty()); // 게시글이 없음

        // then
        assertThrows(CustomException.class, () -> {
            likesService.createLikes(memberName, articleId); // 존재하지 않는 게시글에 대해 삭제 시도
        });
        assertEquals("게시글을 찾을 수 없습니다.",ErrorCode.ARTICLE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("좋아요 삭제 성공") // 유저 A는 유저B가 쓴 게시글에 쓴 좋아요를 삭제해야함
    void delete_likes_success() throws Exception{
        // given
        MemberEntity userA = MemberEntity.builder()
                .id(99)
                .password("1234")
                .email("userA@gmail.com")
                .username("userA")
                .role("ROLE_USER")
                .build();

        MemberEntity userB = MemberEntity.builder()
                .id(88)
                .password("1234")
                .email("userB@gmail.com")
                .username("userB")
                .role("ROLE_USER")
                .build();

        ArticleEntity Barticle = ArticleEntity.builder()
                .id(80)
                .author(userB)
                .title("test title")
                .category(ArticleEntity.ArticleCategories.RECRUIT_BOARD)
                .build();

        LikesEntity likesEntity = LikesEntity.builder()
                .id(44)
                .member(userA)
                .article(Barticle)
                .build();


        // when
        given(likesRepository.findById(likesEntity.getId())).willReturn(Optional.of(likesEntity)); // 좋아요는 존재
        given(articleRepository.findById(Barticle.getId())).willReturn(Optional.of(Barticle));// 다른사람 게시글


        // then
        assertEquals(0,article.getLikesCount());

    }

    @Test
    @DisplayName("좋아요 삭제 실패 - 다른 사용자의 좋아요.권한 없음")
    void delete_likes_fail_different_user() {
        // given
        String memberName = member.getUsername(); // 삭제를 시도하는 사용자
        String anotherName = "another";
        int articleId = article.getId();

        // Mock LikesEntity for another user
        LikesEntity anotherUserLikes = LikesEntity.builder()
                .id(2)
                .article(article)
                .member(member) // 다른 사용자의 좋아요
                .build();

        // Mocking the behavior of repositories
//        Mockito.when(likesService.checkAuth(memberName,articleId)).thenReturn(true);
        // given(likesRepository.findById(1)).willReturn(Optional.of(anotherUserLikes)); // 다른 사용자의 좋아요를 찾음
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article)); // 게시글 존재

        // when & then
        assertThrows(CustomException.class, () -> {
            likesService.deleteLikes(anotherUserLikes.getId(),memberName); // 자신의 좋아요가 아님
        });

        // likesRepository.deleteById(1)가 호출되지 않았는지 확인
        verify(likesRepository, never()).deleteById(1);
        assertEquals("권한이 없습니다.",ErrorCode.NO_AUTHORITY.getMessage());
    }
}
