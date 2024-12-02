package jsl.moum.auth.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jsl.moum.auth.dto.MusicGenre;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.rank.Rank;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import jsl.moum.auth.dto.MemberDto;
import jsl.moum.auth.service.SignupService;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.response.ResponseCode;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
        // given
        MemberDto.Request memberRequestDto = MemberDto.Request.builder()
                .id(1)
                .name("testusername")
                .username("testUser")
                .password("zpzzgjdg@$^1")
                .email("testuser123@gmail.com")
                .profileDescription("Test user profile description")
                .verifyCode("123456")
                .address("123 Street Name, City, Country")
                .proficiency("Advanced")
                .instrument("Guitar")
                .role("USER")
                .profileImageUrl("http://example.com/profile.jpg")
                .videoUrl("http://example.com/video.mp4")
                .genres(List.of(MusicGenre.POP, MusicGenre.CLASSICAL))
                .activeStatus(true)
                .banStatus(false)
                .build();

        MockMultipartFile memberRequestDtoFile = new MockMultipartFile(
                "memberRequestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(memberRequestDto).getBytes()
        );

        when(signupService.signupMember(any(MemberDto.Request.class), any()))
                .thenReturn(memberRequestDto.getUsername());

        // when & then
        mockMvc.perform(multipart("/join")
                        .file(memberRequestDtoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(ResponseCode.REGISTER_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data").value(memberRequestDto.getUsername()));
    }

    @Test
    @DisplayName("회원가입 실패 - 유효성 검증 실패")
    @WithMockUser(username = "testUser")
    void signupMember_fail_validationError() throws Exception {
        // given
        MemberDto.Request memberRequestDto = MemberDto.Request.builder()
                .id(1)
                .name("testusername")
                .username("testUser")
                .password("zpzzgjdg@$^1")
                .email("tesgmail.com") // 잘못된 이메일 형식
                .profileDescription("Test user profile description")
                .verifyCode("123456")
                .address("123 Street Name, City, Country")
                .proficiency("Advanced")
                .instrument("Guitar")
                .role("USER")
                .profileImageUrl("http://example.com/profile.jpg")
                .videoUrl("http://example.com/video.mp4")
                .genres(List.of(MusicGenre.POP, MusicGenre.CLASSICAL))
                .activeStatus(true)
                .banStatus(false)
                .build();

        MockMultipartFile memberRequestDtoFile = new MockMultipartFile(
                "memberRequestDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(memberRequestDto).getBytes()
        );

        doThrow(new CustomException(ErrorCode.INVALID_INPUT_VALUE))
                .when(signupService)
                .signupMember(any(MemberDto.Request.class), any());

        // when & then
        mockMvc.perform(multipart("/join")
                        .file(memberRequestDtoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andExpect(status().isBadRequest()) // HTTP 400
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()))
                .andExpect(jsonPath("$.errors[0].field").value("email"));
    }
    @Test
    @DisplayName("회원 재가입 - 성공")
    @WithMockUser(username = "testUser")
    void reJoinMember_ShouldReturnSuccess() throws Exception {
        // given
        MemberDto.RejoinRequest rejoinRequestDto = new MemberDto.RejoinRequest("testUser","test@gmail.com");

        MemberDto.Response responseDto = new MemberDto.Response(
                1,
                "Test User",
                "testUser",
                "Test user profile description",
                "http://example.com/profile.jpg",
                0,
                List.of(MusicGenre.POP, MusicGenre.CLASSICAL),
                Rank.BRONZE,
                "http://example.com/video.mp4",
                null,
                null
        );



        when(signupService.rejoinMember(anyString()))
                .thenReturn(responseDto);

        // when & then
        mockMvc.perform(patch("/re-join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejoinRequestDto))
                        .with(csrf()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(ResponseCode.REJOIN_SUCCESS.getCode()))
                .andExpect(jsonPath("$.data.username").value("testUser"))
                .andExpect(jsonPath("$.data.name").value("Test User"))
                .andExpect(jsonPath("$.data.profileDescription").value("Test user profile description"));
    }

    @Test
    @DisplayName("회원 재가입 실패 - 회원이 존재하지 않음")
    @WithMockUser(username = "testUser")
    void reJoinMember_fail_notFound() throws Exception {
        // given
        MemberDto.RejoinRequest rejoinRequestDto = new MemberDto.RejoinRequest("testUser","test@gmail.com");


        when(signupService.rejoinMember(anyString()))
                .thenThrow(new CustomException(ErrorCode.MEMBER_NOT_EXIST));

        // when & then
        mockMvc.perform(patch("/re-join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(rejoinRequestDto))
                        .with(csrf()))
                .andExpect(status().isNotFound()) // HTTP 404
                .andExpect(jsonPath("$.code").value(ErrorCode.MEMBER_NOT_EXIST.getCode()));
    }

}