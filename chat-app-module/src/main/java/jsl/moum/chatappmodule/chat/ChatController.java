package jsl.moum.chatappmodule.chat;

import jsl.moum.chatappmodule.auth.service.AuthService;
import jsl.moum.chatappmodule.global.response.ResponseCode;
import jsl.moum.chatappmodule.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatService chatService;
    private final ChatStreamService chatStreamService;
    private final AuthService authService;

    @PostMapping("/{id}")
    public Mono<ResponseEntity<ResultResponse>> sendMessage(@PathVariable(name = "id") Integer id,
                                                                @RequestBody Chat.Request request){
        log.info("ChatController POST /{id} API");
        Mono<Authentication> authMono = authService.getAuthentication();

        return authMono.flatMap(authentication -> {
            String sender = authentication.getPrincipal().toString();
            Chat chat = chatService.buildChat(request, sender, id);
            Mono<Chat> savedChat = chatService.saveChat(chat);

            ResultResponse result = ResultResponse.of(ResponseCode.CHAT_SEND_SUCCESS, new ChatDto(chat));
            return savedChat
                    .map(res -> new ResponseEntity<>(result, HttpStatus.OK)) // map success response
                    .doOnError(error -> log.error("ChatController sendMessage error : {}", error));
        }).onErrorReturn(new ResponseEntity<>(ResultResponse.of(ResponseCode.CHAT_SEND_FAILED, null), HttpStatus.BAD_REQUEST))
                .doFinally(signalType -> log.info("ChatController sendMessage : Signal Type : {}", signalType));
    }

    @GetMapping(value = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatDto> getChatStream(@PathVariable(name = "id") Integer id){
        log.info("ChatController GET /{id} API");

        Flux<ChatDto> recentChats = chatService.getChatsRecentByChatroomId(id);
        Flux<ChatDto> chatFluxStream = chatStreamService.getNewChatStream(id);
        return recentChats.concatWith(chatFluxStream)
                .switchIfEmpty(Flux.defer(() -> {
                    System.err.println("ChatController getChatStream : No chat found, waiting for new entries...");
                    log.info("ChatController getChatStream : No chat found, waiting for new entries...");
                    return Flux.never();
                }))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.error("ChatController getChatStream error : {}", error))
                .doOnCancel(() -> log.info("ChatController getChatStream : Cancelled"))
                .doFinally(signalType -> log.info("ChatController getChatStream : Signal Type : {}", signalType));
    }

    @GetMapping(value = "/{id}/before-timestamp", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatDto> getChatsBeforeTimestamp(@PathVariable(name = "id") Integer id,
                                           @RequestParam(name = "timestamp")
                                           @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) String timestamp) {
        log.info("ChatController GET /{id}/before-timestamp API");
        LocalDateTime lastChatTimestamp = LocalDateTime.parse(timestamp);

        return chatService.getChatsBeforeTimestampByChatroomId(id, lastChatTimestamp)
                .switchIfEmpty(Flux.defer(() -> {
                    log.info("ChatController getChatsBeforeTimestamp : No chats found before timestamp : {}", lastChatTimestamp);
                    return Flux.never();
                }))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.error("ChatController getChatsBeforeTimestamp error: {}", error));
    }

}

