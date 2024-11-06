package jsl.moum.chatappmodule.chat;

import jsl.moum.chatappmodule.auth.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@Slf4j
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final AuthService authService;
    private int PAGE_SIZE = 5;

    public Chat buildTestChat(Chat.TestRequest testRequest, int chatroomId){
        Chat chat = Chat.builder()
                .sender(testRequest.getSender())
                .receiver(testRequest.getReceiver())
                .message(testRequest.getMessage())
                .chatroomId(chatroomId)
                .timestamp(LocalDateTime.now())
                .build();
        log.info("ChatController buildTestChat : {}", chat);
        return chat;
    }

    public Chat buildChat(Chat.Request request, String sender, int chatroomId){
        Chat chat = Chat.builder()
                .sender(sender)
                .receiver(request.getReceiver())
                .message(request.getMessage())
                .chatroomId(chatroomId)
                .timestamp(LocalDateTime.now())
                .build();
        log.info("ChatController buildChat : {}", chat);
        return chat;
    }

    public Mono<Chat> saveChat(Chat chat){
        return chatRepository.save(chat);
    }

    public Flux<ChatDto> getChatsRecentByChatroomId(int chatroomId){
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC ,"timestamp"));
        return chatRepository.findByChatroomId(chatroomId, pageRequest)
                .map(chat -> new ChatDto(chat));
    }

    public Flux<ChatDto> getChatsBeforeTimestampByChatroomId(int chatroomId, LocalDateTime timestamp){
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC ,"timestamp"));
        return chatRepository.findChatsBeforeTimestampByChatroomId(chatroomId, timestamp, pageRequest)
                .map(chat -> new ChatDto(chat));
    }
}
