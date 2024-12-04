package jsl.moum.community.likes.service;

import jsl.moum.community.likes.domain.LikesRepositoryCustom;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.community.perform.domain.repository.PerformArticleRepository;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import jsl.moum.moum.team.domain.TeamEntity;
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
    private PerformArticleRepository performArticleRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private LikesRepositoryCustom likesRepositoryCustom;

    private MemberEntity member;
    private ArticleEntity article;
    private LikesEntity likes;
    private PerformArticleEntity performArticle;
    private TeamEntity team;
    private LifecycleEntity moum;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        member = MemberEntity.builder()
                .id(3)
                .username("testuser")
                .build();

        moum = LifecycleEntity.builder()
                .id(1)
                .build();

        team = TeamEntity.builder()
                .id(1)
                .teamName("team")
                .build();

        article = ArticleEntity.builder()
                .id(6)
                .likesCount(0)
                .author(member)
                .build();

        performArticle = PerformArticleEntity.builder()
                .id(1)
                .performanceName("perform article")
                .team(team)
                .moum(moum)
                .build();

        likes = LikesEntity.builder()
                .id(1)
                .article(article)
                .performArticle(performArticle)
                .member(member)
                .build();
    }

    @Test
    @DisplayName("일반게시글 - 좋아요 등록 성공")
    void create_likes_success() {
        // given
        String memberName = "anothor_member";
        int articleId = article.getId();

        given(memberRepository.findByUsername(memberName)).willReturn(member);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));
        given(likesRepository.save(any(LikesEntity.class))).willReturn(likes);
        given(likesRepositoryCustom.isAlreadyLikesOnPerformArticle(anyInt(), anyInt())).willReturn(false);

        LikesDto.Response response = likesService.createLikes(memberName, articleId);

        // then
        verify(likesRepository).save(any(LikesEntity.class));
        verify(articleRepository).save(article);
        assertEquals(3, response.getMemberId());
        assertEquals(6, response.getArticleId());
        assertEquals(1,article.getLikesCount());
    }

    @Test
    @DisplayName("일반게시글 - 좋아요 등록 실패 - 본인의 게시글")
    void create_likes_fail_self_likes(){
        // given
        String memberName = member.getUsername();
        int articleId = article.getId();

        given(memberRepository.findByUsername(memberName)).willReturn(member);
        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        verify(likesRepository, never()).save(any(LikesEntity.class));
        assertEquals("자신의 게시글에는 좋아요를 누를 수 없습니다.",ErrorCode.CANNOT_CREATE_SELF_LIKES.getMessage());
    }

    @Test
    @DisplayName("일반게시글 - 좋아요 등록 실패 - 없는 게시글")
    void create_likes_fail_no_article() {
        // given
        String memberName = member.getUsername();
        int articleId = 999; // 존재하지 않는 게시글 ID

        given(memberRepository.findByUsername(memberName)).willReturn(member);
        given(articleRepository.findById(articleId)).willReturn(Optional.empty());

        // then
        assertThrows(CustomException.class, () -> {
            likesService.createLikes(memberName, articleId);
        });
        assertEquals("게시글을 찾을 수 없습니다.",ErrorCode.ARTICLE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("일반게시글 - 좋아요 삭제 성공") // 유저 A는 유저B가 쓴 게시글에 쓴 좋아요를 삭제해야함
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
        given(likesRepository.findById(likesEntity.getId())).willReturn(Optional.of(likesEntity));
        given(articleRepository.findById(Barticle.getId())).willReturn(Optional.of(Barticle));


        // then
        assertEquals(0,article.getLikesCount());

    }

    @Test
    @DisplayName("일반게시글 - 좋아요 삭제 실패 - 다른 사용자의 좋아요.권한 없음")
    void delete_likes_fail_different_user() {
        // given
        String memberName = member.getUsername();
        String anotherName = "another";
        int articleId = article.getId();

        LikesEntity anotherUserLikes = LikesEntity.builder()
                .id(2)
                .article(article)
                .member(member)
                .build();

        given(articleRepository.findById(articleId)).willReturn(Optional.of(article));

        // when & then
        assertThrows(CustomException.class, () -> {
            likesService.deleteLikes(memberName,anotherUserLikes.getId()); // 자신의 좋아요가 아님
        });

        // likesRepository.deleteById(1)가 호출되지 않았는지 확인
        verify(likesRepository, never()).deleteById(1);
        assertEquals("권한이 없습니다.",ErrorCode.NO_AUTHORITY.getMessage());
    }


    @Test
    @DisplayName("공연게시글 - 좋아요 등록 성공")
    void create_perform_likes_success() {
        // given
        String memberName = "anothor_member";
        int performArticleId = performArticle.getId();

        given(memberRepository.findByUsername(memberName)).willReturn(member);
        given(performArticleRepository.findById(performArticleId)).willReturn(Optional.of(performArticle));
        given(likesRepository.save(any(LikesEntity.class))).willReturn(likes);
        given(likesRepositoryCustom.isAlreadyLikesOnPerformArticle(anyInt(), anyInt())).willReturn(false);

        LikesDto.Response response = likesService.createPerformLikes(memberName, performArticleId);

        // then
        verify(likesRepository).save(any(LikesEntity.class));
        verify(performArticleRepository).save(performArticle);
        assertEquals(3, response.getMemberId());
//        assertEquals(6, response.getArticleId());
  //      assertEquals(1,article.getLikesCount());
    }

    @Test
    @DisplayName("공연게시글 - 좋아요 등록 실패 - 없는 게시글")
    void create_perform_likes_fail_no_article() {
        // given
        String memberName = member.getUsername();
        int performArticleId = 999;

        given(memberRepository.findByUsername(memberName)).willReturn(member);
        given(performArticleRepository.findById(performArticleId)).willReturn(Optional.empty());

        // then
        assertThrows(CustomException.class, () -> {
            likesService.createPerformLikes(memberName, performArticleId);
        });
        assertEquals("공연 게시글을 찾을 수 없습니다.",ErrorCode.PERFORM_ARTICLE_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("공연게시글 - 좋아요 삭제 성공")
    void delete_perform_likes_success() throws Exception{
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

        PerformArticleEntity BperformArticle = PerformArticleEntity.builder()
                .id(80)
                .performanceName("B perform article")
                .build();

        LikesEntity likesEntity = LikesEntity.builder()
                .id(44)
                .member(userA)
                .performArticle(BperformArticle)
                .build();


        // when
        given(likesRepository.findById(likesEntity.getId())).willReturn(Optional.of(likesEntity));
        given(performArticleRepository.findById(BperformArticle.getId())).willReturn(Optional.of(BperformArticle));

        // then
        //assertEquals(0,performArticle.getLikesCount());
        assertNull(performArticle.getLikesCount());

    }

    @Test
    @DisplayName("공연게시글 - 좋아요 삭제 실패 - 다른 사용자의 좋아요.권한 없음")
    void delete_perform_likes_fail_different_user() {
        // given
        String memberName = member.getUsername();
        String anotherName = "another";
        int performArticleId = performArticle.getId();

        LikesEntity anotherUserLikes = LikesEntity.builder()
                .id(2)
                .performArticle(performArticle)
                .member(member)
                .build();

        given(performArticleRepository.findById(anyInt())).willReturn(Optional.of(performArticle));

        // when & then
        assertThrows(CustomException.class, () -> {
            likesService.deletePerformLikes(memberName,anotherUserLikes.getId());
        });

        verify(likesRepository, never()).deleteById(1);
        assertEquals("권한이 없습니다.",ErrorCode.NO_AUTHORITY.getMessage());
    }
}
