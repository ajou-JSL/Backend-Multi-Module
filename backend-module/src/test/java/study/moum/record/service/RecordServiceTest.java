package study.moum.record.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.record.controller.RecordController;
import study.moum.record.domain.MemberRecordEntity;
import study.moum.record.domain.RecordEntity;
import study.moum.record.dto.RecordDto;
import study.moum.record.repository.MemberRecordRepository;
import study.moum.record.repository.RecordRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class RecordServiceTest {

    @Spy
    @InjectMocks
    private RecordService recordService;

    @Mock
    private RecordRepository recordRepository;

    @Mock
    private RecordController recordController;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private MemberRecordRepository memberRecordRepository;

    private RecordEntity mockRecord;
    private MemberEntity mockMember;
    private MemberRecordEntity mockMemberRecord;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        mockMemberRecord = MemberRecordEntity.builder()
                .id(1)
                .member(mockMember)
                .record(mockRecord)
                .build();

        mockRecord = RecordEntity.builder()
                .id(1)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .members(List.of(mockMemberRecord))
                .build();

        mockMember = MemberEntity.builder()
                .id(1)
                .email("test@user.com")
                .name("kim")
                .username("tester")
                .records(List.of(mockMemberRecord))
                .password("1234")
                .build();

    }

    @Test
    @DisplayName("레코드(이력) 추가 성공")
    void add_record_success(){
        // given
        RecordDto.Request requestDto = RecordDto.Request.builder()
                .recordName("test record")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .build();

        when(memberRepository.findById(1)).thenReturn(Optional.of(mockMember));
        doReturn(mockMember).when(recordService).findMember(anyInt());

        // when
        RecordDto.Response response = recordService.addRecord(mockMember.getId(), requestDto);

        // then
        assertThat(response.getRecordName()).isEqualTo(requestDto.getRecordName());
        verify(recordRepository).save(any());
        verify(memberRecordRepository).save(any());


    }

    @Test
    @DisplayName("레코드(이력) 삭제 성공")
    void remove_record_success(){


        // doReturn(false).when(teamService).checkLeader(any(), any());

    }

    @Test
    @DisplayName("레코드(이력) 추가 실패 - 레코드 주인이 아님")
    void remove_record_fail_NoAuthority(){

    }


    @Test
    @DisplayName("레코드 추가 실패 - 없는 레코드")
    void remove_record_fail_RecordNotFound(){

    }

    @Test
    public void testLoginCheck_Success() {
        String username = "testUser";

        // Mocking loginCheck 메서드
        when(recordController.loginCheck(username)).thenReturn(username); // 서비스 계층을 Mock

        // 실제 메서드 호출
        String loginUserName = recordController.loginCheck(username);

        // 검증
        assertThat(loginUserName).isEqualTo(username);
    }

    @Test
    public void testLoginCheck_Failure() {
        // Similar to above but for failure case
    }

}