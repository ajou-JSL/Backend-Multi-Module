package jsl.moum.backendmodule.chatroom.controller;

import jsl.moum.backendmodule.chatroom.domain.Chatroom;
import jsl.moum.backendmodule.chatroom.service.ChatroomService;
import jsl.moum.backendmodule.global.response.ResponseCode;
import jsl.moum.backendmodule.global.response.ResultResponse;
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

        List<Chatroom> chatroomList = chatroomService.getChatroomListByMemberId(memberId);

        ResultResponse response = ResultResponse.of(ResponseCode.CHATROOM_LIST_GET_SUCCESS, chatroomList);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

}
