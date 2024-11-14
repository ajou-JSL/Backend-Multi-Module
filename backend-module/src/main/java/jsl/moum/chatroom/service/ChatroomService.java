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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ChatroomService {

    @Value("${ncp.object-storage.bucket}")
    private String bucket;

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

        List<Integer> memberIds = chatroomMemberRepository.findAllMemberIdByChatroomId(chatroomId);
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
        log.info("createChatroom method");
        if(isChatroomExists(requestDto)){
            log.error("chatroom already exists");
            throw new CustomException(ErrorCode.CHATROOM_CREATE_FAIL);
        }

        String originalFilename = chatroomImageFile.getOriginalFilename();
        String key = "chatrooms/" + requestDto.getName() + "/" + originalFilename;
        String fileUrl = storageService.uploadFile(key, chatroomImageFile);

        Chatroom chatroom = new Chatroom();
        log.info("Created new chatroom entity");

        if(requestDto.getTeamId() == null){
            chatroom = buildPersonalChatroom(requestDto, fileUrl);
        } else {
            chatroom = buildTeamChatroom(requestDto, fileUrl);
        }
        log.info("Chatroom has been built");

        chatroom = chatroomRepository.saveAndFlush(chatroom);

        log.info("Chatroom saved to repository");

        addChatroomMembers(chatroom.getId(), requestDto.getMembers());

        log.info("Finish createChatroom method");
        return new ChatroomDto(chatroom);
    }

    public ChatroomDto updateChatroom(Integer chatroomId, ChatroomDto.Patch patchDto, MultipartFile chatroomImageFile) throws BadRequestException, IOException {
        Chatroom chatroom = chatroomRepository.findById(chatroomId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_UPDATE_FAIL));

        String existingFileUrl = chatroom.getFileUrl();

        if(chatroomImageFile != null && !chatroomImageFile.isEmpty()){
            if(existingFileUrl != null && !existingFileUrl.isEmpty()){
                String existingFileName = existingFileUrl.replace("https://kr.object.ncloudstorage.com/" + bucket + "/", "");
                storageService.deleteFile(existingFileName);
            }

            String key = "chatrooms/" + chatroom.getName() + "/" + chatroomImageFile.getOriginalFilename();
            String newFileUrl = storageService.uploadFile(key, chatroomImageFile);
            chatroom.setFileUrl(newFileUrl);
        }

        chatroom.setName(patchDto.getName());
        chatroomRepository.save(chatroom);
        return new ChatroomDto(chatroom);
    }


    /**
     *
     * Private access methods
     *
     */


    private boolean isChatroomExists(ChatroomDto.Request requestDto){
        log.info("isChatroomExists method");
        if(requestDto.getTeamId() == null){
            log.info("teamId == null");
            if(isPrivateChatroomExists(requestDto)){
                return true;
            } else{
                return false;
            }
        } else {
            log.info("teamId != null");
            if(isGroupChatroomExists(requestDto)){
                return true;
            } else{
                return false;
            }
        }
    }

    private boolean isGroupChatroomExists(ChatroomDto.Request requestDto){
        return chatroomRepository.existsByTeamId(requestDto.getTeamId());
    }

    private boolean isPrivateChatroomExists(ChatroomDto.Request requestDto){
        log.info("isPrivateChatroomExists");
        if(requestDto.getMembers().size() !=2){
            log.error("Incorrect number of members in the private chatroom");
            throw new CustomException(ErrorCode.CHATROOM_CREATE_FAIL);
        }

        Integer senderId = requestDto.getMembers().get(0);
        Integer receiverId = requestDto.getMembers().get(1);

        MemberEntity sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_CREATE_FAIL));
        MemberEntity receiver = memberRepository.findById(receiverId)
                .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_CREATE_FAIL));

        log.info("isPrivateChatroomExists get member entities");

        List<Integer> commonChatroomIds = Optional.ofNullable(chatroomMemberRepository.findCommonChatroomIds(sender, receiver))
                .orElse(List.of());
        if(commonChatroomIds.isEmpty()) {return false;}

        log.info("Get chatroomIds");

        for(Integer chatroomId : commonChatroomIds){
            Chatroom chatroom = chatroomRepository.findById(chatroomId)
                    .orElseThrow(() -> new CustomException(ErrorCode.CHATROOM_CREATE_FAIL));
            if(chatroom.getType() == 0){
                return true;
            }
        }
        return false;
    }

    private void addChatroomMembers(int chatroomId, List<Integer> memberIds){
        log.info("addChatroomMembers method");
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
        log.info("buildPersonalChatroom method");
        return Chatroom.builder()
                .name(requestDto.getName())
                .type(requestDto.getType())
                .team(null)
                .createdAt(LocalDateTime.now())
                .lastChat(null)
                .lastTimestamp(null)
                .fileUrl(fileUrl)
                .build();
    }

    private Chatroom buildTeamChatroom(ChatroomDto.Request requestDto, String fileUrl){
        log.info("buildTeamChatroom method");
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
