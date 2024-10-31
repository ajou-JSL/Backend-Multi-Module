package jsl.moum.backendmodule.chatroom.service;

import jsl.moum.backendmodule.chatroom.domain.Chatroom;
import jsl.moum.backendmodule.chatroom.domain.ChatroomRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;

    public List<Chatroom> getChatroomListByMemberId(Integer memberId) {
        // Add method for sorting, etc later on
        return chatroomRepository.findByMemberId(memberId);
    }

}
