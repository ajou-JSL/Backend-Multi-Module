package study.moum.email.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import study.moum.email.dto.EmailDto;
import study.moum.email.dto.VerifyDto;
import study.moum.email.service.EmailService;
import study.moum.global.error.ErrorCode;
import study.moum.global.response.ResponseCode;
import study.moum.config.redis.util.RedisUtil;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EmailControllerTest {

    @Mock
    private EmailService emailService;

    @Mock
    private RedisUtil redisUtil;

    @InjectMocks
    private EmailController emailController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(emailController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("이메일 인증 코드 발송 성공 테스트")
    void EmailSendCodeSuccess() throws Exception {
        // Given
        String validEmail = "test@example.com";
        EmailDto.Request emailDto = new EmailDto.Request();
        emailDto.setEmail(validEmail);

        // when
        when(emailService.sendCertificationMail(emailDto)).thenReturn("123456");

        // then
        mockMvc.perform(post("/send-mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(emailDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value("인증 이메일 발송 성공하였습니다."))
                .andDo(print());

        // Argument(s) are different: Wanted: Actual invocations have different arguments: 해결하기
        // https://mag1c.tistory.com/455
        // verify(emailService).sendCertificationMail(emailDto);
        verify(emailService).sendCertificationMail(refEq(emailDto));
    }

    @Test
    @DisplayName("이메일 형식이 잘못된 경우 실패 테스트")
    void EmailSendFail_InvalidEmailForm() throws Exception {
        // given
        String invalidEmail = "invalid-email-form";
        EmailDto.Request request = new EmailDto.Request();
        request.setEmail(invalidEmail);

        // then
        mockMvc.perform(post("/send-mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("이메일이 비어있는 경우 실패 테스트")
    void EmailSendFail_EmptyEmailForm() throws Exception {
        // given
        String emptyEmail = "";
        EmailDto.Request request = new EmailDto.Request();
        request.setEmail(emptyEmail);

        // when, then
        mockMvc.perform(post("/send-mail")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    @Test
    @DisplayName("인증 코드 검증 성공")
    void verifyCodeSuccess() throws Exception {
        // given
        VerifyDto.Request request = new VerifyDto.Request();
        request.setEmail("test@example.com");
        request.setVerifyCode("123456");

        VerifyDto.Response response = new VerifyDto.Response();
        response.setResult(true);
        response.setVerifyCode("123456");

        when(emailService.verifyCode(any(VerifyDto.Request.class))).thenReturn(response);

        // when, then
        mockMvc.perform(post("/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(ResponseCode.EMAIL_VERIFY_SUCCESS.getMessage()))
                .andDo(print());
    }
    @Test
    @DisplayName("인증 코드가 일치하지 않는 경우 실패")
    void verifyCodeFail() throws Exception {
        // given
        VerifyDto.Request request = new VerifyDto.Request();
        request.setEmail("test@example.com");
        request.setVerifyCode("wrong-code");

        VerifyDto.Response response = new VerifyDto.Response();
        response.setResult(false);  // 인증 실패를 표현
        response.setVerifyCode("123456");

        // 이메일 서비스가 인증에 실패한 응답을 반환하도록 설정
        when(emailService.verifyCode(any(VerifyDto.Request.class))).thenReturn(response);

        // when, then
        mockMvc.perform(post("/verify-code")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().is4xxClientError())
                .andExpect(jsonPath("$.status").value(422))
                .andExpect(jsonPath("$.message").value(ErrorCode.EMAIL_VERIFY_FAILED.getMessage()))
                .andDo(print());
    }
}
