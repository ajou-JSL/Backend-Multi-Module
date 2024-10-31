package jsl.moum.chatroom.controller;

import jsl.moum.chatroom.domain.Chatroom;
import jsl.moum.chatroom.dto.ChatroomDto;
import jsl.moum.chatroom.service.ChatroomService;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import jsl.moum.chatroom.service.ChatroomService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
public class ChatroomController {

    private final ChatroomService chatroomService;

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ResultResponse> getChatroomListByMemberId(@PathVariable(name = "memberId") Integer memberId) {
        // Add method for sorting, etc later on in the Service class

        List<ChatroomDto> chatroomList = chatroomService.getChatroomListByMemberId(memberId);

        ResultResponse response = ResultResponse.of(ResponseCode.CHATROOM_LIST_GET_SUCCESS, chatroomList);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

}
