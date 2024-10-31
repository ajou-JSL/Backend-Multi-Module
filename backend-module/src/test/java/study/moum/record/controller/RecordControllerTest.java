package study.moum.record.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import study.moum.auth.domain.entity.MemberEntity;
import study.moum.custom.WithAuthUser;
import study.moum.global.response.ResponseCode;
import study.moum.record.domain.RecordEntity;
import study.moum.record.dto.RecordDto;
import study.moum.record.service.RecordService;

import java.time.LocalDate;
import java.util.ArrayList;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(RecordController.class)
class RecordControllerTest {

    @MockBean
    private RecordService recordService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private RecordDto.Request recordRequestDto;
    private MemberEntity mockMember;
    private RecordEntity mockRecord;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                .apply(springSecurity())
                .alwaysDo(print())
                .build();

        mockMember = MemberEntity.builder()
                .id(1)
                .records(new ArrayList<>())
                .teams(new ArrayList<>())
                .password("1234")
                .name("testuser")
                .build();

        mockRecord = RecordEntity.builder()
                .id(1)
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .members(new ArrayList<>())
                .recordName("test Record")
                .build();

        recordRequestDto = RecordDto.Request.builder()
                .recordName("test Record")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .build();
    }


    @Test
    @DisplayName("레코드(이력) 추가 성공")
    @WithAuthUser
    void add_record_success() throws Exception {
        // given
        RecordDto.Request requestDto = RecordDto.Request.builder()
                .recordName("test record")
                .startDate(LocalDate.now())
                .endDate(LocalDate.now().plusDays(30))
                .build();

        RecordEntity mockRecordEntity = requestDto.toEntity();
        RecordDto.Response response = new RecordDto.Response(mockRecordEntity);

        // when
        //when(recordService.addRecord(mockMember.getId(), requestDto)).thenReturn(response);
        when(recordService.addRecord(anyInt(), any())).thenReturn(response);

        // then
        mockMvc.perform(MockMvcRequestBuilders.post("/api/records/{memberId}", mockMember.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto))
                        .with(csrf()))
                .andExpect(jsonPath("$.status").value(201))
                .andExpect(jsonPath("$.message").value(ResponseCode.RECORD_ADD_SUCCESS.getMessage()))
                .andExpect(jsonPath("$.data.recordName").value("test record"));
    }

    @Test
    @DisplayName("레코드(이력) 추가 실패 - 로그인 필요")
    @WithAuthUser
    void add_record_fail_needLogin(){

    }

    @Test
    @DisplayName("레코드(이력) 삭제 성공")
    @WithAuthUser
    void remove_record_success(){

    }

    @Test
    @DisplayName("레코드(이력) 삭제 실패 - 로그인 필요")
    @WithAuthUser
    void remove_record_fail_needLogin(){

    }


}