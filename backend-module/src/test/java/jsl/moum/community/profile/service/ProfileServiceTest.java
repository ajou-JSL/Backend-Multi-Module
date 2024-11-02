package jsl.moum.community.profile.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.member_profile.dto.ProfileDto;
import jsl.moum.member_profile.service.ProfileService;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.objectstorage.StorageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.*;

class ProfileServiceTest {

    @Spy
    @InjectMocks
    private ProfileService profileService;

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private StorageService storageService;

    private MemberEntity mockMember;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);

        mockMember = MemberEntity.builder()
                .id(1)
                .username("testUser")
                .name("name")
                .password("zpzzgjdg@$^1")
                .email("tester123@gmail.com")
                .teams(new ArrayList<>())
                .records(new ArrayList<>())
                .profileImageUrl("old.jpg")
                .build();

    }

    @Test
    @DisplayName("프로필 조회 성공")
    void get_profile_success(){
        // given
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.of(mockMember));
        doReturn(mockMember).when(profileService).findMember(anyInt());

        ProfileDto.Response response = profileService.getProfile(mockMember.getId());

        // then
        assertThat(response.getUsername()).isEqualTo("testUser");
    }


    @Test
    @DisplayName("프로필 조회 실패 - 타겟 유저 없음")
    void get_profile_fail_no_targetUser(){
        // given
        when(memberRepository.findById(mockMember.getId())).thenReturn(Optional.empty());

        // then
        assertThatThrownBy(() -> profileService.getProfile(mockMember.getId()))
                .isInstanceOf(CustomException.class)
                .hasMessage(ErrorCode.MEMBER_NOT_EXIST.getMessage());
    }

    @Test
    @DisplayName("프로필 수정 성공")
    void update_profile_success() throws IOException {
        // given
        ProfileDto.UpdateRequest updateRequestDto = ProfileDto.UpdateRequest.builder()
                .name("name")
                .username("testuser")
                .address("new address")
                .profileDescription("new description")
                .instrument("piano")
                .build();

        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("new.jpg");
        when(file.isEmpty()).thenReturn(false);
        when(storageService.uploadFile(anyString(), any(MultipartFile.class)))
                .thenReturn("new.jpg");

        // when
        doReturn(mockMember).when(profileService).findMember(mockMember.getUsername());
        doReturn(true).when(profileService).isOwner(mockMember,mockMember.getId());
        when(memberRepository.findByUsername(mockMember.getUsername())).thenReturn((mockMember));

        // when
        ProfileDto.Response response = profileService.updateProfile(mockMember.getUsername(), mockMember.getId(), updateRequestDto, file);

        // then
        assertThat(response.getAddress()).isEqualTo("new address");
        verify(storageService).deleteFile(any());
        verify(memberRepository).save(mockMember);

    }

    @Test
    @DisplayName("프로필 수정 실패 - 로그인 유저(==타겟유저) 없음")
    void update_profile_fail_no_targetUser(){
        // given
        String loginUserName = mockMember.getUsername();
        int targetMemberId = mockMember.getId();

        // when
        when(memberRepository.findByUsername(loginUserName)).thenReturn(null);

        CustomException exception = assertThrows(CustomException.class, () -> {
            profileService.updateProfile(loginUserName, targetMemberId, null, null);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.MEMBER_NOT_EXIST);
    }


    @Test
    @DisplayName("프로필 수정 실패 - 주인이 아님")
    void updateProfile_NotOwner() {

        // given
        String loginUserName = mockMember.getUsername();
        int targetMemberId = mockMember.getId();

        // when
        when(memberRepository.findByUsername(loginUserName)).thenReturn(mockMember);
        when(memberRepository.findById(targetMemberId)).thenReturn(Optional.of(mockMember));
        doReturn(false).when(profileService).isOwner(mockMember, targetMemberId);

        CustomException exception = assertThrows(CustomException.class, () -> {
            profileService.updateProfile(loginUserName, targetMemberId, null, null);
        });

        // then
        assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.NO_AUTHORITY);
        verify(memberRepository, never()).save(mockMember);
    }

}