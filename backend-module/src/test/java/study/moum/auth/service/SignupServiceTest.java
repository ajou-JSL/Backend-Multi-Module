package study.moum.auth.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.multipart.MultipartFile;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.auth.dto.MemberDto;
import study.moum.objectstorage.StorageService;
import study.moum.global.error.ErrorCode;
import study.moum.global.error.exception.CustomException;
import study.moum.global.error.exception.DuplicateUsernameException;
import study.moum.config.redis.util.RedisUtil;

import java.io.IOException;

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
    private RedisUtil redisUtil;

    @InjectMocks
    private SignupService signupService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 중복된 유저네임")
    void SignupFail_UsernameAlreadyExists() {
        // Given
        MemberDto.Request memberRequestDto = MemberDto.Request.builder()
                .id(1)
                .username("existingUser")
                .password("password123")
                .build();

        // When
        when(memberRepository.existsByUsername(memberRequestDto.getUsername())).thenReturn(true);

        // Then
        DuplicateUsernameException thrown = assertThrows(DuplicateUsernameException.class, () -> {
            signupService.signupMember(memberRequestDto, any());
        });

        // Check the exception message
        assertEquals(ErrorCode.USER_NAME_ALREADY_EXISTS, thrown.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 실패 테스트 - 이메일 인증 코드 불일치")
    void SignupFail_EmailVerifyFailed() {
        // Given
        MemberDto.Request memberRequestDto = MemberDto.Request.builder()
                .id(2)
                .username("newUser")
                .password("password123")
                .email("test@example.com")
                .verifyCode("123456")
                .build();

        // Mock username not existing
        when(memberRepository.existsByUsername(memberRequestDto.getUsername())).thenReturn(false);
        // Redis에서 잘못된 인증 코드 반환
        when(redisUtil.getData(memberRequestDto.getEmail())).thenReturn("654321");

        // Then
        CustomException thrown = assertThrows(CustomException.class, () -> {
            signupService.signupMember(memberRequestDto,any());
        });

        assertEquals(ErrorCode.EMAIL_VERIFY_FAILED, thrown.getErrorCode());
    }

    @Test
    @DisplayName("회원가입 성공 테스트 - 인증 코드 일치")
    void SignupSuccess_EmailVerifySuccess_And_NewUsername() throws IOException {
        // Given
        MemberDto.Request memberRequestDto = MemberDto.Request.builder()
                .username("newUser")
                .password("password123")
                .email("test@example.com")
                .verifyCode("123456")
                .build();

        // Mock username check, Redis 인증 코드, 비밀번호 인코딩
        when(memberRepository.existsByUsername(memberRequestDto.getUsername())).thenReturn(false);
        when(redisUtil.getData(memberRequestDto.getEmail())).thenReturn("123456");
        when(bCryptPasswordEncoder.encode(memberRequestDto.getPassword())).thenReturn("encodedPassword");

        MemberEntity savedMemberEntity = MemberEntity.builder()
                .username(memberRequestDto.getUsername())
                .password("encodedPassword")
                .email(memberRequestDto.getEmail())
                //.role("ROLE_USER")
                .build();

        // Mock 저장 동작
        when(memberRepository.save(any(MemberEntity.class))).thenReturn(savedMemberEntity);

        // Mock MultipartFile 생성
        MultipartFile file = mock(MultipartFile.class);
        when(file.getOriginalFilename()).thenReturn("profile.jpg");
        when(file.getBytes()).thenReturn("test file content".getBytes()); // Mocking file content

        // When
        signupService.signupMember(memberRequestDto, file); // 파일을 포함한 회원가입 호출

        // Verify 저장된 엔티티 정보
        ArgumentCaptor<MemberEntity> memberEntityCaptor = ArgumentCaptor.forClass(MemberEntity.class);
        verify(memberRepository).save(memberEntityCaptor.capture());
        MemberEntity capturedMemberEntity = memberEntityCaptor.getValue();

        assertEquals("newUser", capturedMemberEntity.getUsername());
        assertEquals("encodedPassword", capturedMemberEntity.getPassword());
        assertEquals("test@example.com", capturedMemberEntity.getEmail());
//        assertEquals("ROLE_USER", capturedMemberEntity.getRole());

        // Redis에서 인증 코드가 호출되었는지 확인
        verify(redisUtil).getData(memberRequestDto.getEmail());
        // 비밀번호 인코딩이 호출되었는지 확인
        verify(bCryptPasswordEncoder).encode(memberRequestDto.getPassword());
    }
}
