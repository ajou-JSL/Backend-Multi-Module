package jsl.moum.chatroom.service;

import jsl.moum.chatroom.domain.Chatroom;
import jsl.moum.chatroom.domain.ChatroomRepository;
import jsl.moum.chatroom.dto.ChatroomDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.when;

class ChatroomServiceTest {

    @Spy
    @InjectMocks
    private ChatroomService chatroomService;

    @Mock
    private ChatroomRepository chatroomRepository;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

//    @Test
//    @DisplayName("멤버 아이디로 채팅방 찾기 성공")
//    void get_chatroom_by_memberId_success(){
//        // given
//        int memberId = 1;
//        Chatroom chatroom1 = new Chatroom(1, "A", 1, memberId);
//        Chatroom chatroom2 = new Chatroom(2, "B", 1, memberId);
//
//        when(chatroomRepository.findByMemberId(memberId))
//                .thenReturn(Arrays.asList(chatroom1, chatroom2));
//
//        // when
//        List<ChatroomDto> result = chatroomService.getChatroomListByMemberId(memberId);
//
//        // then
//        assertThat(result.size()).isEqualTo(2);
//        assertThat(result.get(0).getName()).isEqualTo("A");
//        assertThat(result.get(1).getName()).isEqualTo("B");
//
//    }
    /*

    public List<ChatroomDto> getChatroomListByMemberId(Integer memberId) {
        // Add method for sorting, etc later on

        List<ChatroomDto> chatroomList = new ArrayList<>();

        for(Chatroom chatroom : chatroomRepository.findByMemberId(memberId)){
            chatroomList.add(new ChatroomDto(chatroom));
        }

        return chatroomList;
    }
     */

}