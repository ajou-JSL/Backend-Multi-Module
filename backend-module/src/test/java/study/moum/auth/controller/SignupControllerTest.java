package study.moum.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.auth.dto.MemberDto;
import study.moum.auth.service.SignupService;
import study.moum.global.error.ErrorCode;
import study.moum.global.error.exception.DuplicateUsernameException;
import study.moum.global.response.ResponseCode;
import study.moum.moum.team.dto.TeamDto;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SignupController.class)
class SignupControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private SignupService signupService;

    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("회원가입 - 성공")
    @WithMockUser(username = "testUser")
    void signupMember_ShouldReturnSuccess() throws Exception {
        // Given
        MemberDto.Request memberRequestDto = MemberDto.Request.builder()
                .id(1)
                .username("testUser")
                .verifyCode("123456")
                .name("name")
                .password("zpzzgjdg@$^1")
                .email("testuser123@gmail.com")
                .build();

        // ArticleRequestDto를 JSON으로 변환하여 MockMultipartFile로 생성
        MockMultipartFile memberRequestDtoFile = new MockMultipartFile("memberRequestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(memberRequestDto).getBytes());

        // When
        doNothing().when(signupService).signupMember(any(MemberDto.Request.class), any());

        // Then
        mockMvc.perform(multipart("/join")
                        .file(memberRequestDtoFile) // MemberRequestDto 추가
                        .contentType(MediaType.MULTIPART_FORM_DATA) // Content-Type 설정
                        .with(csrf())) // CSRF 토큰 추가
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.REGISTER_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(memberRequestDto.getUsername()));
    }


    @Test
    @DisplayName("회원가입 실패 - 유효성 검증 실패")
    @WithMockUser(username = "testUser")
    void signupMember_fail_validationError() throws Exception {
        // Given
        MemberDto.Request memberRequestDto = MemberDto.Request.builder()
                .id(1)
                .username("testUser")
                .verifyCode("123")
                .name("namesfsdfsdffsdfdfd")
                .password("123")
                .email("te12mail.com")
                .build();

        // ArticleRequestDto를 JSON으로 변환하여 MockMultipartFile로 생성
        MockMultipartFile memberRequestDtoFile = new MockMultipartFile("memberRequestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(memberRequestDto).getBytes());

        // When
        doNothing().when(signupService).signupMember(any(MemberDto.Request.class), any());

        // Then
        mockMvc.perform(multipart("/join")
                        .file(memberRequestDtoFile) // MemberRequestDto 추가
                        .contentType(MediaType.MULTIPART_FORM_DATA) // Content-Type 설정
                        .with(csrf())) // CSRF 토큰 추가
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }


}