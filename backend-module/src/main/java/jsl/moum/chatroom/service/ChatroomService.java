package jsl.moum.chatroom.service;

import jsl.moum.chatroom.domain.Chatroom;
import jsl.moum.chatroom.domain.ChatroomRepository;
import jsl.moum.chatroom.dto.ChatroomDto;
import jsl.moum.chatroom.domain.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;

    public List<ChatroomDto> getChatroomListByMemberId(Integer memberId) {
        // Add method for sorting, etc later on

        List<ChatroomDto> chatroomList = new ArrayList<>();

        for(Chatroom chatroom : chatroomRepository.findByMemberId(memberId)){
            chatroomList.add(new ChatroomDto(chatroom));
        }

        return chatroomList;
    }

}
