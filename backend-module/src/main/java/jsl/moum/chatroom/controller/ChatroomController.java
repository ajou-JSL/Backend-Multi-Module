package jsl.moum.chatroom.controller;

import jsl.moum.chatroom.dto.ChatroomDto;
import jsl.moum.chatroom.dto.ChatroomMemberInfoDto;
import jsl.moum.chatroom.service.ChatroomService;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.global.response.ResponseCode;
import jsl.moum.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/api/chatroom")
@Slf4j
public class ChatroomController {

    private final ChatroomService chatroomService;

    @GetMapping("/member/{memberId}")
    public ResponseEntity<ResultResponse> getChatroomListByMemberId(@PathVariable(name = "memberId") Integer memberId) {
        // Add method for sorting, etc later on in the Service class

        List<ChatroomDto> chatroomList = chatroomService.getChatroomListByMemberId(memberId);

        ResultResponse response = ResultResponse.of(ResponseCode.CHATROOM_LIST_GET_SUCCESS, chatroomList);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @PostMapping("")
    public ResponseEntity<ResultResponse> createChatroom(@RequestPart(value = "chatroomInfo") ChatroomDto.Request requestDto,
                                            @RequestPart(value = "chatroomProfile", required = false) MultipartFile chatroomImageFile) throws IOException {
        log.info("/api/chatroom createChatroom");
        ChatroomDto chatroomDto = chatroomService.createChatroom(requestDto, chatroomImageFile);
        log.info("createChatroom method successfully executed");

        ResultResponse response = ResultResponse.of(ResponseCode.CHATROOM_CREATE_SUCCESS, chatroomDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResultResponse> getChatroomById(@PathVariable(name = "id") Integer chatroomId) {
        try {
            ChatroomDto chatroomDto = chatroomService.getChatroomById(chatroomId);
            ResultResponse response = ResultResponse.of(ResponseCode.CHATROOM_FIND_SUCCESS, chatroomDto);
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
        } catch (BadRequestException e) {
            throw new CustomException(ErrorCode.CHATROOM_FIND_FAIL);
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<ResultResponse> updateChatroom(@PathVariable(name = "id") Integer chatroomId,
                                                         @RequestPart(name = "chatroomInfo") ChatroomDto.Patch patchDto,
                                                         @RequestPart(name = "chatroomProfile") MultipartFile chatroomImageFile) throws IOException {
        ChatroomDto chatroomDto = chatroomService.updateChatroom(chatroomId, patchDto, chatroomImageFile);
        ResultResponse response = ResultResponse.of(ResponseCode.CHATROOM_UPDATE_SUCCESS, chatroomDto);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }

    @GetMapping("/{id}/member")
    public ResponseEntity<?> getChatroomMemberList(@PathVariable(name = "id") Integer chatroomId) {

        List<ChatroomMemberInfoDto> chatroomList = chatroomService.getChatroomMemberList(chatroomId);

        ResultResponse response = ResultResponse.of(ResponseCode.CHATROOM_MEMBER_LIST_GET_SUCCESS, chatroomList);
        return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
    }


    @PostMapping("/{id}/member")
    public ResponseEntity<ResultResponse> addChatroomMember(@PathVariable(name = "id") Integer chatroomId,
                                                            @RequestBody ChatroomDto.Members members) {
        chatroomService.inviteChatroomMembers(chatroomId, members);
        try {
            ChatroomDto chatroomDto = chatroomService.getChatroomById(chatroomId);
            ResultResponse response = ResultResponse.of(ResponseCode.CHATROOM_INVITE_SUCCESS, chatroomDto);
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
        } catch (BadRequestException e) {
            throw new CustomException(ErrorCode.CHATROOM_MEMBER_INVITE_FAIL);
        }
    }

    @DeleteMapping("/{id}/member")
    public ResponseEntity<ResultResponse> removeChatroomMember(@PathVariable(name = "id") Integer chatroomId,
                                                               @RequestBody ChatroomDto.Members members) {
        chatroomService.removeChatroomMembers(chatroomId, members);
        try {
            ChatroomDto chatroomDto = chatroomService.getChatroomById(chatroomId);
            ResultResponse response = ResultResponse.of(ResponseCode.CHATROOM_MEMBER_REMOVE_SUCCESS, chatroomDto);
            return new ResponseEntity<>(response, HttpStatus.valueOf(response.getStatus()));
        } catch (BadRequestException e) {
            throw new CustomException(ErrorCode.CHATROOM_MEMBER_REMOVE_FAIL);
        }
    }
}
