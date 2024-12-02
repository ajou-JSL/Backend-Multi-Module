package jsl.moum.auth.service;

import jsl.moum.common.CommonService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.objectstorage.StorageService;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.error.exception.DuplicateUsernameException;
import jsl.moum.config.redis.util.RedisUtil;

import java.io.IOException;
import java.util.ArrayList;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class SignupServiceTest {

    @Mock
    private MemberRepository memberRepository;

    @Mock
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Mock
    private StorageService storageService;

    @Mock
    private CommonService commonService;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private SignupService signupService;

    private MemberDto.Request memberRequestDto;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        memberRequestDto = MemberDto.Request.builder()
                .id(1)
                .username("newUser")
                .password("password123")
                .email("test@example.com")
                .verifyCode("123456")
                .build();
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 중복된 유저네임(활성화된 계정)")
    void SignupFail_UsernameAlreadyExists() {
        // given
        String username = "existingUser";

        // when
        MemberEntity existingMember = new MemberEntity();
        existingMember.setUsername(username);
        existingMember.setActiveStatus(true);

        when(memberRepository.findByUsername(memberRequestDto.getUsername())).thenReturn(existingMember);

        // then
        DuplicateUsernameException thrown = assertThrows(DuplicateUsernameException.class, () -> {
            signupService.signupMember(memberRequestDto, any());
        });

        assertThat(thrown.getMessage()).isEqualTo("이미 존재하는 아이디입니다.");
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 밴된 계정")
    void SignupFail_BannedAccount() throws IOException {
        // given
        String username = "bannedUser";

        // when
        MemberEntity bannedMember = new MemberEntity();
        bannedMember.setUsername(username);
        bannedMember.setBanStatus(true);

        when(memberRepository.findByUsername(memberRequestDto.getUsername())).thenReturn(bannedMember);

        // then
        String result = signupService.signupMember(memberRequestDto, any());

        assertTrue(result.contains("밴 당한 계정입니다."));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 탈퇴한 계정")
    void SignupFail_DeletedAccount() throws IOException {
        // given
        String username = "deletedUser";

        // when
        MemberEntity deletedMember = new MemberEntity();
        deletedMember.setUsername(username);
        deletedMember.setActiveStatus(false);

        when(memberRepository.findByUsername(memberRequestDto.getUsername())).thenReturn(deletedMember);

        // then
        String result = signupService.signupMember(memberRequestDto, any());

        assertTrue(result.contains("회원 탈퇴한 계정입니다."));
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 이메일 인증 코드 불일치")
    void SignupFail_EmailVerifyFailed() {
        // given
        String wrongCode = "654321";
        MemberDto.Request memberRequestDto = MemberDto.Request.builder()
                .id(2)
                .username("newUser")
                .password("password123")
                .email("test@example.com")
                .verifyCode("123456")
                .build();

        when(memberRepository.findByUsername(memberRequestDto.getUsername())).thenReturn(null);
        when(redisUtil.getData(memberRequestDto.getEmail())).thenReturn(wrongCode);

        // then
        CustomException thrown = assertThrows(CustomException.class, () -> {
            signupService.signupMember(memberRequestDto, any());
        });

        assertEquals(ErrorCode.EMAIL_VERIFY_FAILED, thrown.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 성공 테스트 - 프로필 이미지 업로드 포함")
    void signup_success_with_file() throws IOException {
        // given
        String verifyCode = "123456";
        MemberDto.Request memberRequestDto = MemberDto.Request.builder()
                .username("newUser")
                .password("password123")
                .email("test@example.com")
                .verifyCode(verifyCode)
                .records(new ArrayList<>())
                .build();

        when(memberRepository.findByUsername(memberRequestDto.getUsername())).thenReturn(null);
        when(redisUtil.getData(memberRequestDto.getEmail())).thenReturn(verifyCode);
        when(bCryptPasswordEncoder.encode(memberRequestDto.getPassword())).thenReturn("encodedPassword");

        MemberEntity savedMemberEntity = MemberEntity.builder()
                .username(memberRequestDto.getUsername())
                .password("encodedPassword")
                .email(memberRequestDto.getEmail())
                .build();

        when(memberRepository.save(any(MemberEntity.class))).thenReturn(savedMemberEntity);

        // file
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("profile.jpg");
        when(file.getBytes()).thenReturn("test file content".getBytes());

        // when
        signupService.signupMember(memberRequestDto, file);

        ArgumentCaptor<MemberEntity> memberEntityCaptor = ArgumentCaptor.forClass(MemberEntity.class);
        verify(memberRepository).save(memberEntityCaptor.capture());
        MemberEntity capturedMemberEntity = memberEntityCaptor.getValue();

        assertEquals("newUser", capturedMemberEntity.getUsername());
        assertEquals("encodedPassword", capturedMemberEntity.getPassword());
        assertEquals("test@example.com", capturedMemberEntity.getEmail());

        verify(redisUtil).getData(memberRequestDto.getEmail());
        verify(bCryptPasswordEncoder).encode(memberRequestDto.getPassword());
    }

    @Test
    @DisplayName("회원가입 성공 테스트 - 프로필 이미지 업로드 없이")
    void signup_success_without_file() throws IOException {
        // given
        String verifyCode = "123456";
        MemberDto.Request memberRequestDto = MemberDto.Request.builder()
                .username("newUserWithoutImage")
                .password("password123")
                .email("noimage@example.com")
                .verifyCode(verifyCode)
                .build();

        when(memberRepository.findByUsername(memberRequestDto.getUsername())).thenReturn(null);
        when(redisUtil.getData(memberRequestDto.getEmail())).thenReturn(verifyCode);
        when(bCryptPasswordEncoder.encode(memberRequestDto.getPassword())).thenReturn("encodedPassword");

        MemberEntity savedMemberEntity = MemberEntity.builder()
                .username(memberRequestDto.getUsername())
                .password("encodedPassword")
                .email(memberRequestDto.getEmail())
                .build();

        when(memberRepository.save(any(MemberEntity.class))).thenReturn(savedMemberEntity);

        // when
        signupService.signupMember(memberRequestDto, null);

        ArgumentCaptor<MemberEntity> memberEntityCaptor = ArgumentCaptor.forClass(MemberEntity.class);
        verify(memberRepository).save(memberEntityCaptor.capture());
        MemberEntity capturedMemberEntity = memberEntityCaptor.getValue();

        assertEquals("newUserWithoutImage", capturedMemberEntity.getUsername());
        assertEquals("encodedPassword", capturedMemberEntity.getPassword());
        assertEquals("noimage@example.com", capturedMemberEntity.getEmail());
    }

    @Test
    @DisplayName("재가입 실패 테스트 - 멤버 없음")
    void rejoin_fail_memberNotFound() {
        // given
        String username = "nonExistentUser";
        when(commonService.findMemberByUsername(username)).thenReturn(null);

        // when & then
        CustomException thrown = assertThrows(CustomException.class, () -> {
            signupService.rejoinMember(username);
        });

        assertEquals(ErrorCode.MEMBER_NOT_EXIST, thrown.getErrorCode());
    }

    @Test
    @DisplayName("재가입 성공 테스트")
    void rejoin_success() {
        // given
        String username = "existingUser";
        MemberEntity member = new MemberEntity();
        member.setUsername(username);
        member.setActiveStatus(false);

        when(commonService.findMemberByUsername(username)).thenReturn(member);

        // when
        MemberDto.Response response = signupService.rejoinMember(username);

        // then
        assertNotNull(response);
        assertEquals(member.getActiveStatus(), true);
        assertEquals(username, response.getUsername());
    }

}