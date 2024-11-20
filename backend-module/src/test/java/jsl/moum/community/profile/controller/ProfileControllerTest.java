package jsl.moum.community.profile.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.member_profile.controller.ProfileController;
import jsl.moum.member_profile.dto.ProfileDto;
import jsl.moum.member_profile.service.ProfileService;
import jsl.moum.custom.WithAuthUser;
import jsl.moum.global.response.ResponseCode;

import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ProfileController.class)
class ProfileControllerTest {

    @MockBean
    private ProfileService profileService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MemberEntity mockMember;


    @BeforeEach
    void setUp(){
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        mockMember = MemberEntity.builder()
                .id(1)
                .username("test user")
                .teams(new ArrayList<>())
                .records(new ArrayList<>())
                .build();
    }

    @Test
    @DisplayName("프로필 조회 성공")
    @WithAuthUser
    void get_profile_success() throws Exception {
        // given
        ProfileDto.Response responseDto = new ProfileDto.Response(mockMember);

        // when
        when(profileService.getProfile(anyInt())).thenReturn(responseDto);

        // then
        mockMvc.perform(MockMvcRequestBuilders.get("/api/profiles/{memberId}", mockMember.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.status").value(200))
                .andExpect(jsonPath("$.message").value(ResponseCode.GET_PROFILE_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.username").value("test user"));
    }


    @Test
    @DisplayName("프로필 수정 성공")
    @WithAuthUser
    void update_profile_success() throws Exception {
        // given
        ProfileDto.UpdateRequest updateRequestDto = ProfileDto.UpdateRequest.builder()
                .name("name")
                .username("testuser")
                .address("address")
                .profileDescription("description")
                .instrument("piano")
                .build();

        ProfileDto.Response response = new ProfileDto.Response(mockMember);

        // when
        when(profileService.updateProfile(anyString(), anyInt(), any(), any())).thenReturn(response);

        MockMultipartFile file = new MockMultipartFile("file", "testfile.jpg", MediaType.IMAGE_JPEG_VALUE, "test file content".getBytes());
        MockMultipartFile profileRequestDtoFile = new MockMultipartFile("updateProfileDto",
                "",
                MediaType.APPLICATION_JSON_VALUE,
                objectMapper.writeValueAsString(updateRequestDto).getBytes());

        // then
        // MockMvcRequestBuilders.multipart 는 기본적으로 POST 요청이라 PATCH로 달아주고 요청보내야한다.
        mockMvc.perform(MockMvcRequestBuilders.multipart(HttpMethod.PATCH,"/api/profiles/{memberId}", mockMember.getId())
                        .file(file)
                        .file(profileRequestDtoFile)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .with(csrf()))
                .andDo(print())
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.UPDATE_PROFILE_SUCCESS.getMessage()));
    }
}