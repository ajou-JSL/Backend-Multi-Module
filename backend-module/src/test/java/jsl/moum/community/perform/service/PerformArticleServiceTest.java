package jsl.moum.community.perform.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.community.perform.domain.entity.PerformArticleEntity;
import jsl.moum.community.perform.domain.repository.PerformArticleRepository;
import jsl.moum.community.perform.domain.repository.PerformArticleRepositoryCustom;
import jsl.moum.community.perform.dto.PerformArticleDto;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import jsl.moum.moum.lifecycle.domain.repository.LifecycleRepository;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberRepositoryCustom;
import jsl.moum.moum.team.domain.TeamRepository;
import jsl.moum.objectstorage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class PerformArticleServiceTest {

    @Spy
    @InjectMocks
    private PerformArticleService performArticleService;

    @Mock
    private PerformArticleRepository performArticleRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private TeamMemberRepositoryCustom teamMemberRepositoryCustom;

    @Mock
    private PerformArticleRepositoryCustom performArticleRepositoryCustom;

    @Mock
    private LifecycleRepository lifecycleRepository;

    private MemberEntity mockMember;
    private TeamEntity mockTeam;
    private PerformArticleDto.Request mockRequestDto;
    private LifecycleEntity mockMoum;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMember = MemberEntity.builder()
                .id(1)
                .username("testUser")
                .teams(new ArrayList<>())
                .records(new ArrayList<>())
                .build();

        mockTeam = TeamEntity.builder()
                .id(1)
                .leaderId(mockMember.getId())
                .records(new ArrayList<>())
                .members(new ArrayList<>())
                .build();

        mockMoum = LifecycleEntity.builder()
                .id(1)
                .build();

        mockRequestDto = PerformArticleDto.Request.builder()
                .performanceName("Test Performance")
                .performanceDescription("Description")
                .performanceLocation("Location")
                .performancePrice(100)
                .teamId(1)
                .membersId(List.of(1))
                .build();
    }

    @Test
    @DisplayName("공연게시글 생성 성공")
    void create_perform_article_success() throws IOException {
        // given
        MultipartFile mockFile = mock(MultipartFile.class);
        String mockImageUrl = "http://example.com/test.jpg";
        when(storageService.uploadFile(anyString(), eq(mockFile))).thenReturn(mockImageUrl);
        when(memberRepository.findByUsername(anyString())).thenReturn(mockMember);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockMoum));
        when(teamMemberRepositoryCustom.findAllMembersByTeamId(anyInt())).thenReturn(List.of(mockMember));
        doReturn(mockTeam).when(performArticleService).findTeam(anyInt());
        doReturn(true).when(performArticleService).isLeader(any(),any());

        // when
        PerformArticleDto.Response response = performArticleService.createPerformArticle("testUser", mockRequestDto, mockFile);

        // then
        assertNotNull(response);
        assertEquals("Test Performance", response.getPerformanceName());
    }

    @Test
    @DisplayName("공연게시글 생성 실패 - 리더가 아님")
    void create_perfor_article_fail_not_leader() throws IOException {
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockMember);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(performArticleService.findTeam(anyInt())).thenReturn(mockTeam);
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockMoum));
        doReturn(mockTeam).when(performArticleService).findTeam(anyInt());
        doReturn(false).when(performArticleService).isLeader(any(),any());
        MultipartFile mockFile = mock(MultipartFile.class);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            performArticleService.createPerformArticle("testUser", mockRequestDto, mockFile);
        });

        assertEquals("NO_AUTHORITY", exception.getErrorCode().name());
    }

    @Test
    @DisplayName("공연게시글 단건 조회 성공")
    void get_PerformArticle_ById_success() {
        // given
        PerformArticleEntity mockArticle = PerformArticleEntity.builder()
                .id(1)
                .performanceName("Test Performance")
                .team(mockTeam)
                .moum(mockMoum)
                .build();
        when(performArticleRepository.findById(anyInt())).thenReturn(Optional.of(mockArticle));

        // when
        PerformArticleDto.Response response = performArticleService.getPerformArticleById(1);

        // then
        assertNotNull(response);
        assertEquals("Test Performance", response.getPerformanceName());
    }

    @Test
    @DisplayName("공연게시글 단건 조회 실패 - 없는 게시글")
    void get_PerformArticle_ById_fail_article_notfound() {
        // given
        when(performArticleRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            performArticleService.getPerformArticleById(1);
        });

        assertEquals("ILLEGAL_ARGUMENT", exception.getErrorCode().name());
    }

    @Test
    @DisplayName("공연게시글 리스트 조회 성공")
    void get_AllPerformArticle_success() {
        // given
        PerformArticleEntity mockArticle1 = PerformArticleEntity.builder()
                .id(1)
                .performanceName("Test Performance 1")
                .team(mockTeam)
                .moum(mockMoum)
                .build();
        PerformArticleEntity mockArticle2 = PerformArticleEntity.builder()
                .id(2)
                .performanceName("Test Performance 2")
                .team(mockTeam)
                .moum(mockMoum)
                .build();
        Page<PerformArticleEntity> page = new PageImpl<>(List.of(mockArticle1, mockArticle2));

        when(performArticleRepository.findAll(PageRequest.of(0, 10))).thenReturn(page);

        // when
        List<PerformArticleDto.Response> responseList = performArticleService.getAllPerformArticle(0, 10);

        // then
        assertNotNull(responseList);
        assertEquals(2, responseList.size());
        assertEquals("Test Performance 1", responseList.get(0).getPerformanceName());
        assertEquals("Test Performance 2", responseList.get(1).getPerformanceName());
    }

    @Test
    @DisplayName("이달의 공연 게시글 리스트 조회 성공")
    void get_all_thismonth_perform_articles_suceess() {
        // given
        // 시작일자 이번달인거
        PerformArticleEntity mockArticle1 = PerformArticleEntity.builder()
                .id(1)
                .performanceName("Performance 1")
                .performanceStartDate(new Date())
                .moum(mockMoum)
                .team(mockTeam)
                .build();
        PerformArticleEntity mockArticle2 = PerformArticleEntity.builder()
                .id(2)
                .performanceName("Performance 2")
                .performanceStartDate(new Date())
                .moum(mockMoum)
                .team(mockTeam)
                .build();

        // 시작일자 2025년인거
        PerformArticleEntity mockArticleA = PerformArticleEntity.builder()
                .id(1)
                .performanceName("Performance 1")
                .performanceStartDate(java.sql.Date.valueOf("2025-11-15"))
                .team(mockTeam)
                .moum(mockMoum)
                .build();

        PerformArticleEntity mockArticleB = PerformArticleEntity.builder()
                .id(2)
                .performanceName("Performance 2")
                .performanceStartDate(java.sql.Date.valueOf("2025-11-15"))
                .team(mockTeam)
                .moum(mockMoum)
                .build();

        Page<PerformArticleEntity> mockArticles = new PageImpl<>(List.of(mockArticle1, mockArticle2));
        when(performArticleRepositoryCustom.getThisMonthPerformArticles(any(Pageable.class)))
                .thenReturn(mockArticles);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));

        // when
        Page<PerformArticleDto.Response> responseList = performArticleService.getAllThisMonthPerformArticles(0, 10);

        // then
        assertNotNull(responseList);
        assertEquals(1, responseList.getTotalPages());
    }


    @Test
    @DisplayName("findMember 메서드 성공")
    void findMember_method_success() {
        // given
        String username = mockMember.getUsername();
        when(memberRepository.findByUsername(username)).thenReturn(mockMember);

        // when
        MemberEntity foundMember = performArticleService.findMember(username);

        // then
        assertThat(foundMember.getUsername()).isEqualTo(username);

    }


    @Test
    @DisplayName("findMember 메서드 실패 - 없는 멤버")
    void findMember_method_success_test() {
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(null);

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            performArticleService.findMember("nonExistentUser");
        });

        assertEquals("MEMBER_NOT_EXIST", exception.getErrorCode().name());
    }

    @Test
    @DisplayName("findTeam 메서드 성공")
    void findTeam_method_success() {
        // given
        int teamId = mockTeam.getId();
        when(teamRepository.findById(teamId)).thenReturn(Optional.of(mockTeam));

        // when
        TeamEntity foundTeam = performArticleService.findTeam(teamId);

        // then
        assertThat(foundTeam.getId()).isEqualTo(teamId);
    }

    @Test
    @DisplayName("findTeam 메서드 실패 - 없는 팀")
    void findTeam_method_fail_teamnotfound() {
        // given
        when(teamRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when & then
        CustomException exception = assertThrows(CustomException.class, () -> {
            performArticleService.findTeam(999);
        });

        assertEquals("TEAM_NOT_FOUND", exception.getErrorCode().name());
    }

    @Test
    @DisplayName("isLeader 메서드 성공")
    void isLeader_method_success() {
        // when
        boolean isLeader = performArticleService.isLeader(mockTeam, mockMember);

        // then
        assertTrue(isLeader);
    }

    @Test
    @DisplayName("isLeader 메서드 실패 - 리더가 아님")
    void isLeader_method_fail_notLeader() {
        // given
        mockTeam.setLeaderId(2); // 다른 리더 ID 설정

        // when
        boolean isLeader = performArticleService.isLeader(mockTeam, mockMember);

        // then
        assertFalse(isLeader);
    }


}
