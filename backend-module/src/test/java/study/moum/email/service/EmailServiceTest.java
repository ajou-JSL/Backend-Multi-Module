package study.moum.email.service;

import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;
import study.moum.auth.domain.repository.MemberRepository;
import study.moum.email.dto.EmailDto;
import study.moum.email.dto.VerifyDto;
import study.moum.global.error.exception.AlreadyVerifiedEmailException;
import study.moum.global.error.exception.CustomException;
import study.moum.config.redis.util.RedisUtil;


import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender javaMailSender;

    @Mock
    private RedisUtil redisUtil;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private EmailService emailService;

    @Mock
    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @DisplayName("이메일 메시지 생성 테스트")
    void createMailMessage() throws Exception {
        // given
        String email = "test@example.com";
        EmailDto.Request emailDto = new EmailDto.Request();
        emailDto.setEmail(email);
        String code = "123456";

        when(memberRepository.existsByEmail(email)).thenReturn(false);  // 중복 이메일 아님
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage); // MimeMessage 생성
        doNothing().when(javaMailSender).send(any(MimeMessage.class)); // send 메서드에 대한 동작 설정

        // when
        emailService.sendMail(emailDto, code);

        // then
        verify(javaMailSender).send(any(MimeMessage.class)); // send 메서드가 호출되었는지 확인
    }


    @Test
    @DisplayName("이미 가입된 이메일로 인증 시도 시 실패 테스트")
    void SendMailFail_AlreadyVerifiedEmail() {
        // Given
        String email = "duplicate@example.com";
        EmailDto.Request emailDto = new EmailDto.Request();
        emailDto.setEmail(email);

        when(memberRepository.existsByEmail(email)).thenReturn(true); // 중복된 이메일 체크 걸림

        // when, then
        assertThrows(AlreadyVerifiedEmailException.class, () -> {
            emailService.sendCertificationMail(emailDto);
        });

        verify(memberRepository).existsByEmail(email);
        verifyNoInteractions(javaMailSender); // 메일 발송이 호출되지 않아야 함
    }

    @Test
    @DisplayName("이메일 인증 코드 발송 성공 테스트")
    void SendMailSuccess() throws Exception {
        // Given
        String email = "test@example.com";
        EmailDto.Request emailDto = new EmailDto.Request();
        emailDto.setEmail(email);
        String verificationCode = "123456";

        when(memberRepository.existsByEmail(email)).thenReturn(false);  // 중복 이메일 아님
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);

        // When
        emailService.sendCertificationMail(emailDto);

        // Then
        verify(memberRepository).existsByEmail(email);  // 중복 체크 호출 확인
        verify(javaMailSender).send(any(MimeMessage.class));  // 메일 전송 확인
//        verify(redisUtil).setDataExpire(email, verificationCode, 60L);  // Redis 저장 확인 -> 왜 진짜 코드가 날아가지?
    }

    @Test
    @DisplayName("메일 전송 중 예외 발생 테스트")
    void SendMailFail_RuntimeException() throws Exception {
        // given
        String email = "test@example.com";
        EmailDto.Request emailDto = new EmailDto.Request();
        emailDto.setEmail(email);

        when(memberRepository.existsByEmail(email)).thenReturn(false);  // 중복 이메일 아님
        when(javaMailSender.createMimeMessage()).thenReturn(mimeMessage);
        doThrow(new RuntimeException("Mail sending failed")).when(javaMailSender).send(any(MimeMessage.class));

        // when, then
        assertThrows(RuntimeException.class, () -> {
            emailService.sendCertificationMail(emailDto);
        });

        verify(memberRepository).existsByEmail(email);  // 중복 체크 호출 확인
        verify(javaMailSender).send(any(MimeMessage.class));  // 메일 전송 시도 확인
    }
    @Test
    @DisplayName("인증 코드 검증 성공")
    void verifyCodeSuccess() {
        // given
        String email = "test@example.com";
        String verifyCode = "123456";

        VerifyDto.Request request = new VerifyDto.Request();
        request.setEmail(email);
        request.setVerifyCode(verifyCode);

        // when
        when(redisUtil.getData(email)).thenReturn(verifyCode);

        // then
        VerifyDto.Response response = emailService.verifyCode(request);

        assertThat(response.getResult()).isTrue();
        assertThat(response.getVerifyCode()).isEqualTo(verifyCode);
        verify(redisUtil).deleteData(email);  // 인증 성공 시 중복 방지로 데이터 삭제 호출되는지 확인
    }

    @Test
    @DisplayName("인증 코드가 존재하지 않는 경우 실패")
    void verifyCodeFail_NoStoredCode() {
        // given
        String email = "test@example.com";
        String wrongCode = "wrong-code";

        VerifyDto.Request request = new VerifyDto.Request();
        request.setEmail(email);
        request.setVerifyCode(wrongCode);

        // when
        when(redisUtil.getData(email)).thenReturn(null);

        // then
        assertThrows(CustomException.class, () -> emailService.verifyCode(request));
    }

    @Test
    @DisplayName("인증 코드가 일치하지 않는 경우 실패")
    void verifyCodeFail_InvalidCode() {
        // given
        String email = "test@example.com";
        String storedCode = "123456";
        String wrongCode = "wrong-code";

        VerifyDto.Request request = new VerifyDto.Request();
        request.setEmail(email);
        request.setVerifyCode(wrongCode);

        when(redisUtil.getData(email)).thenReturn(storedCode);

        // when
        VerifyDto.Response response = emailService.verifyCode(request);

        // then
        assertThat(response.getResult()).isFalse();  // 코드가 일치하지 않으므로 실패
        verify(redisUtil, never()).deleteData(email); // deleteData() 메소드 호출 안됐는지
    }
}
