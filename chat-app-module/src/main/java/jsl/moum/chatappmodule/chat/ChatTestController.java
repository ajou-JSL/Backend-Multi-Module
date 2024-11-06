package jsl.moum.chatappmodule.chat;

import jsl.moum.chatappmodule.global.response.ResponseCode;
import jsl.moum.chatappmodule.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController // 데이터 리턴 서버
@RequestMapping("/api/chat/test")
public class ChatTestController {

    private final ChatService chatService;
    private final ChatStreamService chatStreamService;


    @GetMapping("/")
    public Mono<ResponseEntity<?>> test(){
        log.info("ChatTestController GET /test API");

        Mono<ResultResponse> resultResponse = Mono.just(ResultResponse.of(ResponseCode.TEST_SUCCESS, "ChatController test API"));
        return Mono.just(ResponseEntity.ok().body(resultResponse));
    }

    @PostMapping("/{id}")
    public Mono<ResponseEntity<ResultResponse>> sendMessage(@PathVariable(name = "id") Integer id,
                                                            @RequestBody Chat.TestRequest testRequest){
        log.info("ChatTestController POST /test/{id} API");
        Chat chat = chatService.buildTestChat(testRequest, id);
        Mono<Chat> savedChat = chatService.saveChat(chat);

        ResultResponse result = ResultResponse.of(ResponseCode.CHAT_SEND_SUCCESS, new ChatDto(chat));
        return savedChat
                .map(res -> new ResponseEntity<>(result, HttpStatus.OK)) // map success response
                .doOnError(error -> log.error("ChatTestController testSendMessage error : {}", error))
                .onErrorReturn(new ResponseEntity<>(ResultResponse.of(ResponseCode.CHAT_SEND_FAILED, null), HttpStatus.BAD_REQUEST));
    }

    @GetMapping(value = "/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatDto> getChatStream(@PathVariable(name = "id") Integer id){
        log.info("ChatTestController GET /test/{id} API");

        return chatStreamService.getNewChatStream(id)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.error("ChatTestController testGetMessageStream error : {}", error))
                .doOnCancel(() -> log.info("ChatTestController testGetMessageStream : Cancelled"))
                .doFinally(signalType -> log.info("ChatTestController testGetMessageStream : Signal Type : {}", signalType));
    }

    @GetMapping(value = "/paging/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatDto> getChatStreamPaged(@PathVariable(name = "id") Integer id){
        log.info("ChatTestController GET /test/paging/{id} API");

        Flux<ChatDto> recentChats = chatService.getChatsRecentByChatroomId(id);
        Flux<ChatDto> chatFluxStream = chatStreamService.getNewChatStream(id);
        return recentChats.concatWith(chatFluxStream)
                .switchIfEmpty(Flux.defer(() -> {
                    System.err.println("ChatTestController testGetChatStreamPaged : No chat found, waiting for new entries...");
                    log.info("ChatTestController testGetChatStreamPaged : No chat found, waiting for new entries...");
                    return Flux.never();
                }))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.error("ChatTestController testGetMessageStreamPaging error : {}", error))
                .doOnCancel(() -> log.info("ChatTestController testGetMessageStreamPaging : Cancelled"))
                .doFinally(signalType -> log.info("ChatTestController testGetMessageStreamPaging : Signal Type : {}", signalType));
    }

    @GetMapping(value = "/{id}/string", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> testGetChatPagedAsString(@PathVariable(name = "id") Integer id){
        log.info("ChatTestController GET /test/{id}/string API");

        return chatStreamService.getNewChatStream(id)
                .map(ChatDto::toString)
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.error("ChatTestController testGetChatPagedAsString error : {}", error))
                .doOnCancel(() -> log.info("ChatTestController testGetChatPagedAsString : Cancelled"))
                .doFinally(signalType -> log.info("ChatTestController testGetChatPagedAsString : Signal Type : {}", signalType));
    }

    @GetMapping(value = "/paging/{id}/string", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> testGetChatStreamPagedAsString(@PathVariable(name = "id") Integer id){
        log.info("ChatTestController GET /test/paging/{id}/string API");

        Flux<String> recentChats = chatService.getChatsRecentByChatroomId(id)
                .map(ChatDto::toString);
        Flux<String> chatFluxStream = chatStreamService.getNewChatStream(id)
                .map(ChatDto::toString);
        return recentChats.concatWith(chatFluxStream)
                .switchIfEmpty(Flux.defer(() -> {
                    System.err.println("ChatTestController testGetChatStreamPagedAsString : No chat found, waiting for new entries...");
                    log.info("ChatTestController testGetChatStreamPagedAsString : No chat found, waiting for new entries...");
                    return Flux.never();
                }))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> log.error("ChatTestController testGetChatStreamPagedAsString error : {}", error))
                .doOnCancel(() -> log.info("ChatTestController testGetChatStreamPagedAsString : Cancelled"))
                .doFinally(signalType -> log.info("ChatTestController testGetChatStreamPagedAsString : Signal Type : {}", signalType));
    }

    @GetMapping(value = "/{id}/older", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatDto> testGetOlderChats(@PathVariable(name = "id") Integer id,
                                           @RequestParam(name = "beforeTimestamp") String beforeTimestamp) {
        log.info("ChatTestController GET /test/{id}/older API");
        LocalDateTime timestamp = LocalDateTime.parse(beforeTimestamp);

        return chatService.getChatsBeforeTimestampByChatroomId(id, timestamp)
                .switchIfEmpty(Flux.defer(() -> {
                    System.err.println("ChatTestController testGetOlderChats : No chat found");
                    log.info("ChatTestController testGetOlderChats : No chat found");
                    return Flux.never();
                }))
                .subscribeOn(Schedulers.boundedElastic())
                .timeout(Duration.ofSeconds(10))
                .doOnError(error -> log.error("ChatTestController getOlderMessages error: {}", error));
    }
}