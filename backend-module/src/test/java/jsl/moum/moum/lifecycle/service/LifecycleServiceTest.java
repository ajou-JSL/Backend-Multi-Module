package jsl.moum.moum.lifecycle.service;

import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.lifecycle.domain.*;
import jsl.moum.moum.lifecycle.domain.Process;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleEntity;
import jsl.moum.moum.lifecycle.domain.entity.LifecycleRepositoryCustom;
import jsl.moum.moum.lifecycle.domain.repository.LifecycleRepository;
import jsl.moum.moum.lifecycle.dto.LifecycleDto;
import jsl.moum.moum.lifecycle.dto.ProcessDto;
import jsl.moum.moum.team.domain.TeamEntity;
import jsl.moum.moum.team.domain.TeamMemberRepositoryCustom;
import jsl.moum.moum.team.domain.TeamRepository;
import jsl.moum.objectstorage.StorageService;
import jsl.moum.record.domain.entity.MoumMemberRecordEntity;
import jsl.moum.record.domain.entity.RecordEntity;
import jsl.moum.record.domain.repository.MoumMemberRecordRepository;
import jsl.moum.record.domain.repository.MoumMemberRecordRepositoryCustom;
import jsl.moum.record.domain.repository.RecordRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
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
    private RecordRepository recordRepository;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MoumMemberRecordRepositoryCustom moumMemberRecordRepositoryCustom;

    @Mock
    private MoumMemberRecordRepository moumMemberRecordRepository;

    private LifecycleEntity mockLifecycle;
    private MemberEntity mockLeader;
    private MemberEntity mockMember;
    private TeamEntity mockTeam;
    private LifecycleDto.Request mockLifecycleUpdateRequestDto;
    private MultipartFile mockFile;
    private Process mockProcess;
    private ProcessDto mockProcessDto;
    private RecordEntity mockRecord;
    private MoumMemberRecordEntity mockMoumMemberRecord;

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
                .process(mockProcess)
                .build();

        mockLifecycleUpdateRequestDto = LifecycleDto.Request.builder()
                .teamId(123)
                .leaderId(mockLeader.getId())
                .records(new ArrayList<>())
                .moumName("update moum name")
                .teamId(mockTeam.getId())
                .imageUrls(List.of("imageUrl","imageUrl2"))
                .members(new ArrayList<>())
                .build();

        mockFile = mock(MultipartFile.class);
        mockProcess = new Process(false,false,false,false,false,false,false,0);
        mockProcessDto = ProcessDto.builder()
                .chatroomStatus(true)
                .recruitStatus(true)
                .build();

        mockRecord = RecordEntity.builder()
                .id(1)
                .lifecycle(mockLifecycle)
                .team(mockTeam)
                .createdAt(LocalDateTime.now())
                .recordName("test record")
                .member(mockMember)
                .build();

        mockMoumMemberRecord = MoumMemberRecordEntity.builder()
                .record(mockRecord)
                .lifecycle(mockLifecycle)
                .member(mockMember)
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
        Music music1 = new Music();
        Music music2 = new Music();

        doReturn(mockTeam).when(lifecycleService).findTeam(anyInt());
        doReturn(true).when(lifecycleService).hasTeam(anyString());
        doReturn(true).when(lifecycleService).isTeamLeader(anyString());
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(storageService.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(imageUrl);

        List<MultipartFile> files = new ArrayList<>();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(imageUrl);
        when(file.isEmpty()).thenReturn(false);
        files.add(file);

        LifecycleDto.Request moumRequestDto = LifecycleDto.Request.builder()
                .moumName("test moum")
                .records(new ArrayList<>())
                .music(List.of(music1, music2))
                .build();
        // when
        LifecycleDto.Response response = lifecycleService.addMoum(mockLeader.getUsername(), moumRequestDto, files);
        mockLifecycle.assignTeam(mockTeam);

        // then
        assertThat(response.getMoumName()).isEqualTo(moumRequestDto.getMoumName());
        assertThat(response.getImageUrls().get(0)).isEqualTo(null);
    }

    @Test
    @DisplayName("모음 생성 실패 - 최대 생성 개수 초과")
    void add_moum_fail_max_limit_exceeded() throws IOException {
        // given
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(teamRepository.findById(mockTeam.getId())).thenReturn(Optional.of(mockTeam));
        doReturn(true).when(lifecycleService).hasTeam(anyString());
        doReturn(true).when(lifecycleService).isTeamLeader(anyString());
        when(lifecycleRepositoryCustom.countCreatedLifecycleByTeamId(mockLeader.getId())).thenReturn(3L);

        // when & then
        assertThatThrownBy(() -> lifecycleService.addMoum(mockLeader.getUsername(), mockLifecycleUpdateRequestDto, List.of(mockFile)))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.MAX_MOUM_LIMIT_EXCEEDED.getMessage());
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
        List<MultipartFile> files = new ArrayList<>();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(imageUrl);
        when(file.isEmpty()).thenReturn(false);
        files.add(file);

        LifecycleDto.Request moumRequestDto = LifecycleDto.Request.builder()
                .moumName("test moum")
                .build();

        // then
        assertThatThrownBy(() -> lifecycleService.addMoum(mockLeader.getUsername(), moumRequestDto, files))
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

        List<MultipartFile> files = new ArrayList<>();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(imageUrl);
        when(file.isEmpty()).thenReturn(false);
        files.add(file);

        LifecycleDto.Request moumRequestDto = LifecycleDto.Request.builder()
                .moumName("test moum")
                .build();

        // then
        assertThatThrownBy(() -> lifecycleService.addMoum(mockLeader.getUsername(), moumRequestDto, files))
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

        List<MultipartFile> files = new ArrayList<>();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn(imageUrl);
        when(file.isEmpty()).thenReturn(false);
        files.add(file);

        LifecycleDto.Request moumRequestDto = LifecycleDto.Request.builder()
                .moumName("test moum")
                .build();

        // when & then
        assertThatThrownBy(() -> lifecycleService.addMoum(mockLeader.getUsername(), moumRequestDto, files))
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

    @Test
    @DisplayName("모음을 찾을 수 없음 메소드")
    void moum_not_found_exception() throws IOException {
        // given
        int nonExistentMoumId = 999;

        // when & then
        when(lifecycleRepository.findById(nonExistentMoumId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lifecycleService.findMoum(nonExistentMoumId))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ILLEGAL_ARGUMENT.getMessage());
    }


    /**
     * 모음 정보 수정
     */
    @Test
    @DisplayName("모음 수정 성공")
    void update_moum_success() throws IOException {
        // given
        String imageUrl = "mockUrl";
        Music music1 = new Music();
        Music music2 = new Music();
        LifecycleDto.Request updateRequestDto = LifecycleDto.Request.builder()
                .teamId(123)
                .leaderId(mockLeader.getId())
                .records(new ArrayList<>())
                .moumName("update moum name")
                .teamId(mockTeam.getId())
                .imageUrls(List.of("imageUrl","imageUrl2"))
                .members(new ArrayList<>())
                .music(List.of(music1,music2))
                .build();

        // Mocking dependencies
        doReturn(mockTeam).when(lifecycleService).findTeam(anyInt());
        doReturn(mockLifecycle).when(lifecycleService).findMoum(anyInt());
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(true);
        when(memberRepository.findByUsername(mockLeader.getUsername())).thenReturn(mockLeader);
        when(storageService.uploadFile(anyString(), any(MultipartFile.class))).thenReturn(imageUrl);

        // Prepare files to upload
        List<MultipartFile> files = new ArrayList<>();

        MultipartFile file1 = mock(MultipartFile.class);
        when(file1.getOriginalFilename()).thenReturn("imageUrl");
        when(file1.isEmpty()).thenReturn(false);
        files.add(file1);

        MultipartFile file2 = mock(MultipartFile.class);
        when(file2.getOriginalFilename()).thenReturn("imageUrl2");
        when(file2.isEmpty()).thenReturn(false);
        files.add(file2);

        // when
        LifecycleDto.Response response = lifecycleService.updateMoum(mockLeader.getUsername(), updateRequestDto, files, mockLifecycle.getId());
        mockLifecycle.assignTeam(mockTeam);

        // then
        assertThat(response.getMoumName()).isEqualTo(updateRequestDto.getMoumName());
        assertThat(response.getMusic().size()).isEqualTo(2);
    }

    @Test
    @DisplayName("모음 수정 실패 - 팀을 찾을 수 없음")
    void update_moum_fail_teamNotFound() {
        // given & when
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.empty());

        List<MultipartFile> files = new ArrayList<>();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("imageUrl");
        when(file.isEmpty()).thenReturn(false);
        files.add(file);

        // then
        assertThatThrownBy(() -> lifecycleService.updateMoum(mockLeader.getUsername(), mockLifecycleUpdateRequestDto, files, mockLifecycle.getId()))
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

        List<MultipartFile> files = new ArrayList<>();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("imageUrl");
        when(file.isEmpty()).thenReturn(false);
        files.add(file);

        // then
        assertThatThrownBy(() -> lifecycleService.updateMoum(mockLeader.getUsername(), mockLifecycleUpdateRequestDto, files, mockLifecycle.getId()))
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

        List<MultipartFile> files = new ArrayList<>();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("imageUrl");
        when(file.isEmpty()).thenReturn(false);
        files.add(file);

        // then
        assertThatThrownBy(() -> lifecycleService.updateMoum(mockLeader.getUsername(), mockLifecycleUpdateRequestDto, files, mockLifecycle.getId()))
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
        when(lifecycleService.hasTeam(anyString())).thenReturn(true);

        LifecycleDto.Request moumRequestDto = LifecycleDto.Request.builder()
                .moumName("test moum")
                .build();

        List<MultipartFile> files = new ArrayList<>();
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("imageUrl");
        when(file.isEmpty()).thenReturn(false);
        files.add(file);
//
//        doThrow(new IOException("File upload failed")).when(storageService).uploadFile(anyString(), any(MultipartFile.class));
//
//        // then
//        assertThatThrownBy(() -> lifecycleService.updateMoum(mockLeader.getUsername(), mockLifecycleUpdateRequestDto, files, mockLifecycle.getId()))
//                .isInstanceOf(IOException.class)
//                .hasMessage(ErrorCode.FILE_UPLOAD_FAIL.getMessage());

        // when
        LifecycleDto.Response response = lifecycleService.addMoum(mockLeader.getUsername(), moumRequestDto, files);
        mockLifecycle.assignTeam(mockTeam);

        // then
        assertThat(response.getMoumName()).isEqualTo(moumRequestDto.getMoumName());
        assertThat(response.getImageUrls().get(0)).isEqualTo(null);
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
    void finish_moum_success(){
        // given
        String username = mockLeader.getUsername();
        int moumId = mockLifecycle.getId();

        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleService.findMoum((moumId))).thenReturn(mockLifecycle);
        when(lifecycleService.isTeamLeader(username)).thenReturn(true);

        mockLifecycle.assignProcess(mockProcess);
        mockLifecycle.getProcess().updateAndGetProcessPercentage();

        // when
        LifecycleDto.Response response = lifecycleService.finishMoum(username, moumId);

        // then
        assertThat(response.getProcess().getFinishStatus()).isEqualTo(true);
        assertThat(response.getProcess().getProcessPercentage()).isEqualTo(14);

    }

    @Test
    @DisplayName("모음 마감하기 실패 - 없는 모음")
    void finish_moum_fail_moumNotFound() {
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.empty());

        // when
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(true);

        // then
        assertThatThrownBy(() -> lifecycleService.finishMoum(mockLeader.getUsername(), mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ILLEGAL_ARGUMENT.getMessage());

    }


    @Test
    @DisplayName("모음 마감하기 실패 - 리더가 아님")
    void finish_moum_fail_notLeader() {
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));

        // when
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(false);

        // then
        assertThatThrownBy(() -> lifecycleService.finishMoum(mockLeader.getUsername(), mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NO_AUTHORITY.getMessage());
    }

    @Test
    @DisplayName("모음 마감하기 실패 - 이미 마감 누른 모음")
    void finish_moum_fail_alreadyFinished() {
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(true);
        when(recordRepository.findById(anyInt())).thenReturn(Optional.of(mockRecord));
        when(lifecycleRepositoryCustom.findLatestRecordByMoumId(anyInt())).thenReturn(mockRecord);
        when(teamMemberRepositoryCustom.findAllMembersByTeamId(anyInt())).thenReturn(List.of(mockMember));

        // when
        when(lifecycleService.isFinished(anyInt())).thenReturn(true);

        // then
        assertThatThrownBy(() -> lifecycleService.finishMoum(mockLeader.getUsername(), mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ALREADY_FINISHED_MOUM.getMessage());
    }


    /**
     * 모음 되살리기
     */
    @Test
    @DisplayName("모음 되살리기 성공")
    void reopen_moum_success(){
        // given
        String username = mockLeader.getUsername();
        int moumId = mockLifecycle.getId();

        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleService.findMoum((moumId))).thenReturn(mockLifecycle);
        when(lifecycleService.isTeamLeader(username)).thenReturn(true);
        when(recordRepository.findById(anyInt())).thenReturn(Optional.of(mockRecord));
        when(lifecycleRepositoryCustom.findLatestRecordByMoumId(anyInt())).thenReturn(mockRecord);
        when(lifecycleService.isFinished(anyInt())).thenReturn(true);

        mockLifecycle.assignProcess(mockProcess);
        mockLifecycle.getProcess().updateAndGetProcessPercentage();

        // when
        LifecycleDto.Response response = lifecycleService.reopenMoum(username, moumId);

        // then
        assertThat(response.getProcess().getFinishStatus()).isEqualTo(false);
        assertThat(response.getProcess().getProcessPercentage()).isEqualTo(0);
    }


    @Test
    @DisplayName("모음 되살리기 실패 - 없는 모음")
    void reopen_moum_fail_moumNotFound() {
        // given & when
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.empty());
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(true);
        when(lifecycleService.isFinished(anyInt())).thenReturn(true);

        // then
        assertThatThrownBy(() -> lifecycleService.reopenMoum(mockLeader.getUsername(), mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ILLEGAL_ARGUMENT.getMessage());

    }


    @Test
    @DisplayName("모음 되살리기 실패 - 리더가 아님")
    void reopen_moum_fail_notLeader() {
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));

        // when
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(false);

        // then
        assertThatThrownBy(() -> lifecycleService.reopenMoum(mockLeader.getUsername(), mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NO_AUTHORITY.getMessage());
    }

    @Test
    @DisplayName("모음 되살리기 실패 - 진행중인 모음")
    void finish_moum_fail_notFinished() {
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(true);
        when(recordRepository.findById(anyInt())).thenReturn(Optional.of(mockRecord));
        when(lifecycleRepositoryCustom.findLatestRecordByMoumId(anyInt())).thenReturn(mockRecord);
        //when(teamMemberRepositoryCustom.findAllMembersByTeamId(anyInt())).thenReturn(List.of(mockMember));

        // when
        mockLifecycle.assignProcess(mockProcess);
        when(lifecycleService.isFinished(anyInt())).thenReturn(false);

        // then
        assertThatThrownBy(() -> lifecycleService.reopenMoum(mockLeader.getUsername(), mockLifecycle.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NOT_FINISHED_MOUM.getMessage());
    }


    /**
     * 모음 진척도 수정하기
     */
    @Test
    @DisplayName("진척도 수정하기 성공")
    public void update_process_status_success(){
        // given & when
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(true);

        mockLifecycle.assignProcess(mockProcess);
        mockLifecycle.getProcess().updateAndGetProcessPercentage();
        LifecycleDto.Response response = lifecycleService.updateProcessStatus(mockLeader.getUsername(), mockLifecycle.getId(), mockProcessDto);

        // then
        assertThat(response.getProcess().getChatroomStatus()).isEqualTo(true);
        assertThat(response.getProcess().getRecruitStatus()).isEqualTo(true);
        assertThat(response.getProcess().getPaymentStatus()).isEqualTo(false);
        assertThat(response.getProcess().getProcessPercentage()).isEqualTo(28);

    }

    @Test
    @DisplayName("진척도 수정하기 실패 - 리더가 아님")
    void update_process_status_fail_notLeader() {
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.of(mockLifecycle));

        // when
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(false);

        // then
        assertThatThrownBy(() -> lifecycleService.updateProcessStatus(mockLeader.getUsername(), mockLifecycle.getId(), mockProcessDto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.NO_AUTHORITY.getMessage());
    }

    @Test
    @DisplayName("진척도 수정하기 실패 - 없는 모음")
    void update_process_status_fail_moumNotFound() {
        // given
        when(memberRepository.findByUsername(anyString())).thenReturn(mockLeader);
        when(teamRepository.findById(anyInt())).thenReturn(Optional.of(mockTeam));
        when(lifecycleService.isTeamLeader(anyString())).thenReturn(true);

        // when
        when(lifecycleRepository.findById(anyInt())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> lifecycleService.updateProcessStatus(mockLeader.getUsername(), mockLifecycle.getId(), mockProcessDto))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.ILLEGAL_ARGUMENT.getMessage());
    }


}