package jsl.moum.chatroom.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import jsl.moum.chatroom.dto.ChatroomDto;
import jsl.moum.chatroom.service.ChatroomService;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
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

import java.util.List;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ChatroomController.class)
class ChatroomControllerTest {

    @MockBean
    private ChatroomService chatroomService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .addFilter(new CharacterEncodingFilter("utf-8", true))
                //.apply(springSecurity())
                .alwaysDo(print())
                .build();
    }

//    @Test
//    @DisplayName("멤버아이디로 채팅방 목록 조회 테스트")
//    void get_chatroomList_by_memberId() throws Exception {
//        // given
//        int memberId = 1;
//        ChatroomDto chatroom1 = new ChatroomDto("Chatroom A", 101);
//        ChatroomDto chatroom2 = new ChatroomDto("Chatroom B", 102);
//        List<ChatroomDto> chatroomList = List.of(chatroom1, chatroom2);
//
//        // when
//        when(chatroomService.getChatroomListByMemberId(anyInt())).thenReturn(chatroomList);
//
//        ResultResponse response = ResultResponse.of(ResponseCode.CHATROOM_LIST_GET_SUCCESS, chatroomList);
//
//        // then
//        mockMvc.perform(MockMvcRequestBuilders.get("/api/chatroom/member/{memberId}",memberId)
//                        .contentType(MediaType.APPLICATION_JSON))
//                .andExpect(status().isOk())
//                .andExpect(jsonPath("$.status").value(response.getStatus()))
//                .andExpect(jsonPath("$.message").value(ResponseCode.CHATROOM_LIST_GET_SUCCESS.getMessage()))
//                .andExpect(jsonPath("$.data[0].chatroomName").value(chatroom1.getName()))
//                .andExpect(jsonPath("$.data[0].chatroomId").value(chatroom1.getId()))
//                .andExpect(jsonPath("$.data[1].chatroomName").value(chatroom2.getName()))
//                .andExpect(jsonPath("$.data[1].chatroomId").value(chatroom2.getId()));
//    }


}