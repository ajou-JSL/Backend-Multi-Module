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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
    private MemberEntity mockMember;
    private TeamEntity mockTeam;
    private LifecycleDto.Request mockLifecycleUpdateRequestDto;
    private MultipartFile mockFile;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockLeader = MemberEntity.builder()
                .id(1)
                .teams(new ArrayList<>())
                .username("mock leader")
                .build();

        mockMember = MemberEntity.builder()
                .id(2)
                .teams(new ArrayList<>())
                .username("not leader")
                .build();

        mockTeam = TeamEntity.builder()
                .id(1)
                .members(new ArrayList<>())
                .records(new ArrayList<>())
                .build();

        mockLifecycle = LifecycleEntity.builder()
                .id(1)
                .lifecycleName("test lifecycle")
                .team(mockTeam)
                .records(new ArrayList<>())
                .build();

        mockLifecycleUpdateRequestDto = LifecycleDto.Request.builder()
                .teamId(123)
                .leaderId(mockLeader.getId())
                .records(new ArrayList<>())
                .moumName("update moum name")
                .teamId(mockTeam.getId())
                .imageUrl("imageUrl")
                .members(new ArrayList<>())
                .build();

        mockFile = mock(MultipartFile.class);
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
        doReturn(mockLifecycle).when(lifecycleService).findMoum(anyInt());
        doReturn(false).when(lifecycleService).hasTeam(anyString());

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
     * 팀의 모음 목록 조회
     */
    @Test
    @DisplayName("팀의 모음 목록 조회 성공")
    void get_team_moum_list_success(){
        // given
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleService.findTeam(anyInt())).thenReturn(mockTeam);
        when(lifecycleRepositoryCustom.findLifecyclesByTeamId(anyInt())).thenReturn(List.of(mockLifecycle));

        // when
        List<LifecycleDto.Response> responseList = lifecycleService.getTeamMoumList(mockTeam.getId());

        // then
        assertThat(responseList).isNotEmpty();
        assertThat(responseList.get(0).getMoumName()).isEqualTo(mockLifecycle.getLifecycleName());
    }

    @Test
    @DisplayName("팀의 모음 목록 조회 실패 - 없는 팀")
    void get_team_moum_list_fail_teamNotFound(){
        // given
        //when(lifecycleService.findTeam(anyInt())).thenReturn(null);
        when(lifecycleRepositoryCustom.findLifecyclesByTeamId(anyInt())).thenReturn(List.of(mockLifecycle));

        // when
        // then
        assertThatThrownBy(() -> lifecycleService.findTeam(mockTeam.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.TEAM_NOT_FOUND.getMessage());

    }

    /**
     * 모음 생성
     */
    @Test
    @DisplayName("모음 생성 성공")
    void create_moum_success() throws IOException {
        // given
        String imageUrl = "mockUrl";

        doReturn(mockTeam).when(lifecycleService).findTeam(anyInt());
        doReturn(true).when(lifecycleService).hasTeam(anyString());
        doReturn(true).when(lifecycleService).isTeamLeader(anyString());
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(storageService.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(imageUrl);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(imageUrl);
        when(file.isEmpty()).thenReturn(false);

        LifecycleDto.Request moumRequestDto = LifecycleDto.Request.builder()
                .moumName("test moum")
                .records(new ArrayList<>())
                .build();
        // when
        LifecycleDto.Response response = lifecycleService.addMoum(mockLeader.getUsername(), moumRequestDto, file);
        mockLifecycle.assignTeam(mockTeam);

        // then
        assertThat(response.getMoumName()).isEqualTo(moumRequestDto.getMoumName());
        assertThat(response.getImageUrl()).isEqualTo(imageUrl);
    }

    @Test
    @DisplayName("모음 생성 실패 - 권한 없음")
    void create_moum_fail_no_authority() throws IOException {
        // given & when
        String imageUrl = "mockUrl";

        doReturn(mockLeader).when(lifecycleService).findLoginUser(anyString());
        doReturn(mockTeam).when(lifecycleService).findTeam(anyInt());
        doReturn(true).when(lifecycleService).hasTeam(anyString());
        doReturn(false).when(lifecycleService).isTeamLeader(anyString());
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

        verify(lifecycleRepository, never()).save(any());
    }

    @Test
    @DisplayName("모음 생성 실패 - 가입된 팀이 없음")
    void create_moum_fail_no_team() throws IOException {
        // given & when
        String imageUrl = "mockUrl";

        doReturn(mockTeam).when(lifecycleService).findTeam(anyInt());
        doReturn(mockLeader).when(lifecycleService).findLoginUser(anyString());
        doReturn(false).when(lifecycleService).hasTeam(anyString());
        doReturn(true).when(lifecycleService).isTeamLeader(anyString());

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


    @Test
    @DisplayName("모음 생성 실패 - 팀을 찾을 수 없음")
    void create_moum_fail_team_not_found() throws IOException {
        // given
        int nonExistentTeamId = 999;
        String imageUrl = "mockUrl";

        doReturn(mockLeader).when(lifecycleService).findLoginUser(anyString());
        doReturn(false).when(lifecycleService).hasTeam(anyString());

        when(teamRepository.findById(nonExistentTeamId)).thenReturn(Optional.empty());

        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(storageService.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(imageUrl);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(imageUrl);
        when(file.isEmpty()).thenReturn(false);

        LifecycleDto.Request moumRequestDto = LifecycleDto.Request.builder()
                .moumName("test moum")
                .build();

        // when & then
        assertThatThrownBy(() -> lifecycleService.addMoum(mockLeader.getUsername(), moumRequestDto, file))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.TEAM_NOT_FOUND.getMessage());
    }



    @Test
    @DisplayName("팀을 찾을 수 없음 메소드")
    void team_not_found_exception() throws IOException {
        // given
        int nonExistentTeamId = 999;

        // when & then
        when(teamRepository.findById(nonExistentTeamId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lifecycleService.findTeam(nonExistentTeamId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.TEAM_NOT_FOUND.getMessage());
    }


    /**
     * 모음 정보 수정
     */
    @Test
    @DisplayName("모음 수정 성공")
    void update_moum_success() throws IOException {
        // given
        String imageUrl = "mockUrl";
        LifecycleDto.Request updateRequestDto = LifecycleDto.Request.builder()
                .teamId(123)
                .leaderId(mockLeader.getId())
                .records(new ArrayList<>())
                .moumName("update moum name")
                .teamId(mockTeam.getId())
                .imageUrl(imageUrl)
                .members(new ArrayList<>())
                .build();

        doReturn(mockTeam).when(lifecycleService).findTeam(anyInt());
        doReturn(mockLifecycle).when(lifecycleService).findMoum(anyInt());
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(true);
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(storageService.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(imageUrl);

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(imageUrl);
        when(file.isEmpty()).thenReturn(false);

        // when
        LifecycleDto.Response response = lifecycleService.updateMoum(mockLeader.getUsername(), updateRequestDto, file,mockLifecycle.getId());
        mockLifecycle.assignTeam(mockTeam);

        // then
        assertThat(response.getMoumName()).isEqualTo(updateRequestDto.getMoumName());
    }

    @Test
    @DisplayName("모음 수정 실패 - 팀을 찾을 수 없음")
    void update_moum_fail_teamNotFound() {
        // given & when
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lifecycleService.updateMoum(mockLeader.getUsername(), mockLifecycleUpdateRequestDto, mockFile, mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.TEAM_NOT_FOUND.getMessage());
    }

    @Test
    @DisplayName("모음 수정 실패 - 모음을 찾을 수 없음")
    void update_moum_fail_moumNotFound() {
        // given & when
        when(memberRepository.findByUsername("leader")).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lifecycleService.updateMoum(mockLeader.getUsername(), mockLifecycleUpdateRequestDto, mockFile, mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ILLEGAL_ARGUMENT.getMessage());
    }

    @Test
    @DisplayName("모음 수정 실패 - 팀 리더가 아님")
    void update_moum_fail_notLeader() {
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockMember);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(false);

        // then
        assertThatThrownBy(() -> lifecycleService.updateMoum(mockLeader.getUsername(), mockLifecycleUpdateRequestDto, mockFile, mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NO_AUTHORITY.getMessage());
    }

    @Test
    @DisplayName("모음 수정 실패 - 파일 업로드 중 오류 발생")
    void update_moum_fail_fileUploadError() throws IOException {
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(true);
        doThrow(new IOException("File upload failed")).when(storageService).uploadFile(anyString(), any());

        // then
        assertThatThrownBy(() -> lifecycleService.updateMoum(mockLeader.getUsername(), mockLifecycleUpdateRequestDto, mockFile, mockLifecycle.getId()))
                .isInstanceOf(IOException.class)
                .hasMessage(ErrorCode.FILE_UPLOAD_FAIL.getMessage());
    }
    /**
     * 모음 삭제
     */
    @Test
    @DisplayName("모음 삭제 성공")
    void delete_moum_success(){
        // given & when
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));

        doReturn(true).when(lifecycleService).isTeamLeader(anyString());
        doReturn(mockLifecycle).when(lifecycleService).findMoum(anyInt());

        lifecycleService.deleteMoum(mockLeader.getUsername(), mockLifecycle.getId());


        // then
        verify(lifecycleRepository).deleteById(mockLifecycle.getId());
    }

    @Test
    @DisplayName("모음 삭제 실패 - 없는 모음")
    void delete_moum_failmoumNotFound(){
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(true);

        // then
        assertThatThrownBy(() -> lifecycleService.deleteMoum(mockLeader.getUsername(), mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ILLEGAL_ARGUMENT.getMessage());
    }

    @Test
    @DisplayName("모음 삭제 실패 - 리더가 아님")
    void delete_moum_faile_notLeader(){
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(false);

        // when
        doThrow(new CustomException(ErrorCode.NO_AUTHORITY))
                .when(lifecycleService).isTeamLeader(anyString());

        // then
        assertThatThrownBy(() -> lifecycleService.deleteMoum(mockLeader.getUsername(), mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NO_AUTHORITY.getMessage());
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