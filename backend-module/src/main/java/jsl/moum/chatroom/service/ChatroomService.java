package jsl.moum.chatroom.service;

import jakarta.transaction.Transactional;
import jsl.moum.auth.domain.entity.MemberEntity;
import jsl.moum.auth.domain.repository.MemberRepository;
import jsl.moum.chatroom.domain.*;
import jsl.moum.chatroom.dto.ChatroomDto;
import jsl.moum.chatroom.domain.ChatroomRepository;
import jsl.moum.chatroom.dto.ChatroomMemberInfoDto;
import jsl.moum.global.error.ErrorCode;
import jsl.moum.global.error.exception.CustomException;
import jsl.moum.moum.team.domain.TeamRepository;
import jsl.moum.objectstorage.StorageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static jsl.moum.chatroom.domain.QChatroom.chatroom;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final TeamRepository teamRepository;
    private final ChatroomMemberRepository chatroomMemberRepository;
    private final MemberRepository memberRepository;
    private final StorageService storageService;

    public List<ChatroomDto> getChatroomListByMemberId(Integer memberId) throws CustomException {
        // Add method for sorting, etc later on
        List<ChatroomDto> chatroomList = new ArrayList<>();

        List<ChatroomMember> chatroomMembers = chatroomMemberRepository.findByMemberId(memberId);
        if(chatroomMembers.isEmpty()){
            throw new CustomException(ErrorCode.CHATROOM_LIST_GET_FAIL);
        }

        for(ChatroomMember cm : chatroomMembers){
            chatroomList.add(new ChatroomDto(cm.getChatroom()));
        }

        return chatroomList;
    }

    public List<ChatroomMemberInfoDto> getChatroomMemberList(Integer chatroomId) throws CustomException {
        List<ChatroomMemberInfoDto> chatroomMemberList = new ArrayList<>();

        List<Integer> memberIds = chatroomMemberRepository.findMemberIdByChatroomId(chatroomId);
        if(memberIds.isEmpty()){
            throw new CustomException(ErrorCode.CHATROOM_MEMBER_LIST_GET_FAIL);
        }

        for(Integer memberId : memberIds){
            MemberEntity member = memberRepository.findById(memberId)
                    .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_EXIST));
            chatroomMemberList.add(new ChatroomMemberInfoDto(member));
        }

        return chatroomMemberList;
    }

    public ChatroomDto createChatroom(ChatroomDto.Request requestDto, MultipartFile chatroomImageFile) throws IOException {

        String originalFilename = chatroomImageFile.getOriginalFilename();
        String key = "chatrooms/" + requestDto.getName() + "/" + originalFilename;
        String fileUrl = storageService.uploadFile(key, chatroomImageFile);

        Chatroom chatroom = new Chatroom();

        if(requestDto.getTeamId() == null){
            chatroom = buildPersonalChatroom(requestDto, fileUrl);
        } else {
            chatroom = buildTeamChatroom(requestDto, fileUrl);
        }

        chatroom = chatroomRepository.saveAndFlush(chatroom);
        addChatroomMembers(chatroom.getId(), requestDto.getMembers());

        return new ChatroomDto(chatroom);
    }

    private void addChatroomMembers(int chatroomId, List<Integer> memberIds){
        for(Integer memberId : memberIds){
            ChatroomMember chatroomMember = ChatroomMember.builder()
                    .chatroom(chatroomRepository.findById(chatroomId)
                            .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_CREATE_FAIL)))
                    .member(memberRepository.findById(memberId)
                            .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_CREATE_FAIL)))
                    .build();
            chatroomMemberRepository.save(chatroomMember);
        }
    }

    private Chatroom buildPersonalChatroom(ChatroomDto.Request requestDto, String fileUrl){
        return Chatroom.builder()
                .name(requestDto.getName())
                .type(requestDto.getType())
                .createdAt(LocalDateTime.now())
                .lastChat(null)
                .lastTimestamp(null)
                .fileUrl(fileUrl)
                .build();
    }

    private Chatroom buildTeamChatroom(ChatroomDto.Request requestDto, String fileUrl){
        return Chatroom.builder()
                .name(requestDto.getName())
                .type(requestDto.getType())
                .team(teamRepository.findById(requestDto.getTeamId())
                        .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_CREATE_FAIL)))
                .createdAt(LocalDateTime.now())
                .lastChat(null)
                .lastTimestamp(null)
                .fileUrl(fileUrl)
                .build();
    }


}
