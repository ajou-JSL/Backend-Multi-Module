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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatroomService {

    private final ChatroomRepository chatroomRepository;
    private final ChatroomMemberRepository chatroomMemberRepository;
    private final MemberRepository memberRepository;

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

}
