package jsl.moum.moum.lifecycle.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.lifecycle.domain.*;
import jsl.moum.moum.lifecycle.dto.LifecycleDto;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberRepositoryCustom;
import jsl.moum.moum.team.domain.TeamRepository;
import jsl.moum.moum.team.dto.TeamDto;
import jsl.moum.objectstorage.StorageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class LifecycleServiceTest {

    @Spy
    @InjectMocks
    private LifecycleService lifecycleService;

    @Mock
    private LifecycleRepository lifecycleRepository;

    @Mock
    private TeamMemberRepositoryCustom teamMemberRepositoryCustom;

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private StorageService storageService;

    @Mock
    private LifecycleRepositoryCustom lifecycleRepositoryCustom;

    @Mock
    private MemberRepository memberRepository;

    private LifecycleEntity mockLifecycle;
    private MemberEntity mockLeader;
    private TeamEntity mockTeam;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockLeader = MemberEntity.builder()
                .id(1)
                .teams(new ArrayList<>())
                .username("mock leader")
                .build();

        mockTeam = TeamEntity.builder()
                .id(1)
                .members(new ArrayList<>())
                .build();

        mockLifecycle = LifecycleEntity.builder()
                .id(1)
                .lifecycleName("test lifecycle")
                .team(mockTeam)
                .build();
    }


    /**
     * 모음 조회
     */
    @Test
    @DisplayName("모음 단건 조회 성공")
    void get_moum_byId_success(){
        // given
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(lifecycleRepository.findById(mockLifecycle.getId())).thenReturn(Optional.of(mockLifecycle));
        when(lifecycleService.hasTeam(mockLeader.getUsername())).thenReturn(true);

        // when
        LifecycleDto.Response response = lifecycleService.getMoumById(mockLeader.getUsername(), mockLifecycle.getId());

        // then
        assertThat(response.getMoumName()).isEqualTo(mockLifecycle.getLifecycleName());
    }

    @Test
    @DisplayName("모음 단건 조회 실패 - 팀이 없음")
    void get_moum_byId_fail_no_team(){
        // given & when
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        doReturn(mockLifecycle).when(lifecycleService).findMoum(mockLifecycle.getId());
        doReturn(false).when(lifecycleService).hasTeam(mockLeader.getUsername());

        // then
        assertThatThrownBy(() -> lifecycleService.getMoumById(mockLeader.getUsername(), mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NEED_TEAM.getMessage());

    }

    /**
     * 나의 모음 목록 조회
     */
    @Test
    @DisplayName("나의 모음 목록 조회 성공")
    void get_my_moum_list_success(){
        // given
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(lifecycleService.hasTeam(mockLeader.getUsername())).thenReturn(true);
        when(lifecycleRepositoryCustom.findLifecyclesByUsername(mockLeader.getUsername())).thenReturn(List.of(mockLifecycle));

        // when
        List<LifecycleDto.Response> responseList = lifecycleService.getMyMoumList(mockLeader.getUsername());

        // then
        assertThat(responseList).isNotEmpty();
        assertThat(responseList.get(0).getMoumName()).isEqualTo(mockLifecycle.getLifecycleName());
    }

    /**
     * 모음 생성
     */
    @Test
    @DisplayName("모음 생성 성공")
    void create_moum_success() throws IOException {
        // given
        String imageUrl = "mockUrl";

        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(lifecycleService.hasTeam(mockLeader.getUsername())).thenReturn(true);
        when(lifecycleService.isTeamLeader(mockLeader.getUsername())).thenReturn(true);
        when(storageService.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(imageUrl);
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(imageUrl);
        when(file.isEmpty()).thenReturn(false);

        LifecycleDto.Request moumRequestDto = LifecycleDto.Request.builder()
                .moumName("test moum")
                .build();
        // when
        LifecycleDto.Response response = lifecycleService.addMoum(mockLeader.getUsername(), moumRequestDto, file);

        // then
        assertThat(response.getMoumName()).isEqualTo(moumRequestDto.getMoumName());
        assertThat(response.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    @DisplayName("모음 생성 실패 - 권한 없음")
    void create_moum_fail_no_authority() throws IOException {
        // given & when
        String imageUrl = "mockUrl";

        doReturn(mockLeader).when(lifecycleService).findLoginUser(mockLeader.getUsername());
        doReturn(true).when(lifecycleService).hasTeam(mockLeader.getUsername());
        doReturn(false).when(lifecycleService).isTeamLeader(mockLeader.getUsername());
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);

        when(storageService.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(imageUrl);
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(imageUrl);
        when(file.isEmpty()).thenReturn(false);

        LifecycleDto.Request moumRequestDto = LifecycleDto.Request.builder()
                .moumName("test moum")
                .build();

        // then
        assertThatThrownBy(() -> lifecycleService.addMoum(mockLeader.getUsername(), moumRequestDto, file))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NO_AUTHORITY.getMessage());

    }

    @Test
    @DisplayName("모음 생성 실패 - 가입된 팀이 없음")
    void create_moum_fail_no_team() throws IOException {
        // given & when
        String imageUrl = "mockUrl";

        doReturn(mockLeader).when(lifecycleService).findLoginUser(mockLeader.getUsername());
        doReturn(false).when(lifecycleService).hasTeam(mockLeader.getUsername());
        doReturn(true).when(lifecycleService).isTeamLeader(mockLeader.getUsername());
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);


        when(storageService.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(imageUrl);
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(imageUrl);
        when(file.isEmpty()).thenReturn(false);

        LifecycleDto.Request moumRequestDto = LifecycleDto.Request.builder()
                .moumName("test moum")
                .build();

        // then
        assertThatThrownBy(() -> lifecycleService.addMoum(mockLeader.getUsername(), moumRequestDto, file))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NEED_TEAM.getMessage());
    }

    /**
     * 모음 정보 수정
     */
    @Test
    @DisplayName("모음 수정 성공")
    @Disabled("보류")
    void update_moum_success(){

    }

    /**
     * 모음 삭제
     */
    @Test
    @DisplayName("모음 삭제 성공")
    @Disabled("보류")
    void delete_moum_success(){

    }

    /**
     * 모음 마감하기
     */
    @Test
    @DisplayName("모음 마감하기 성공")
    @Disabled("보류")
    void finish_moum_success(){

    }

    /**
     * 모음 되살리기
     */
    @Test
    @DisplayName("모음 되살리기 성공")
    @Disabled("보류")
    void reopen_moum_success(){

    }

    /**
     * todo : 선택하면 진행률 관련한거 업데이트되는 로직 필요함
     */

}