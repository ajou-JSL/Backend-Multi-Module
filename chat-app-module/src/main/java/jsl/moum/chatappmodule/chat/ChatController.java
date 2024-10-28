package jsl.moum.chatappmodule.chat;

import jsl.moum.chatappmodule.global.response.ResponseCode;
import jsl.moum.chatappmodule.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.awt.print.Pageable;
import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController // 데이터 리턴 서버
@RequestMapping("/api/chat")
public class ChatController {

    private final ChatRepository chatRepository;


    @GetMapping("/test")
    public Mono<ResponseEntity<?>> test(){
        log.info("ChatController GET /test API");

        Mono<ResultResponse> resultResponse = Mono.just(ResultResponse.of(ResponseCode.TEST_SUCCESS, "ChatController test API"));
        return Mono.just(ResponseEntity.ok().body(resultResponse));
    }

    @PostMapping("/test/{id}")
    public Mono<ResponseEntity<ResultResponse>> testSendMessage(@PathVariable(name = "id") Integer id,
                                                                @RequestBody Chat.TestRequest testRequest){
        log.info("ChatController POST /test/{id} API");

        Chat chat = Chat.builder()
                .sender(testRequest.getSender())
                .receiver(testRequest.getReceiver())
                .message(testRequest.getMessage())
                .chatroomId(id)
                .timestamp(LocalDateTime.now())
                .build();

        log.info("ChatController chat : {}", chat);
        Mono<Chat> savedChat = chatRepository.save(chat);
        Mono<ResultResponse> result = Mono.just(ResultResponse.of(ResponseCode.CHAT_SEND_SUCCESS, new ChatDto(chat)));

        return savedChat
                .map(res -> new ResponseEntity<>(ResultResponse.of(ResponseCode.CHAT_SEND_SUCCESS, new ChatDto(res)), HttpStatus.OK)) // map success response
                .doOnError(error -> {
                    log.error("ChatController testSendMessage error : {}", error);
                })
                .onErrorReturn(new ResponseEntity<>(ResultResponse.of(ResponseCode.CHAT_SEND_FAILED, null), HttpStatus.BAD_REQUEST));

//        return result
//                .map(res -> new ResponseEntity<>(res, HttpStatus.OK)) // map success response
//                .doOnError(error -> {
//                    log.error("ChatController testSendMessage error : {}", error);
//                })
//                .onErrorReturn(new ResponseEntity<>(ResultResponse.of(ResponseCode.CHAT_SEND_FAILED, null), HttpStatus.BAD_REQUEST));
    }

    @GetMapping(value = "/test/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatDto> testGetMessageStream(@PathVariable(name = "id") Integer id){
        log.info("ChatController GET /test/{id} API");

        return chatRepository.mFindByChatroomId(id)
                .map(chat -> new ChatDto(chat))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> {
                    log.error("ChatController testGetMessageStream error : {}", error);
                });
    }

    @GetMapping(value = "/test/paging/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatDto> testGetMessageStreamPaging(@PathVariable(name = "id") Integer id){
        log.info("ChatController GET /test/paging/{id} API");

        Flux<ChatDto> recentChats = chatRepository.findByChatroomId(id, PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC ,"timestamp")))
                .map(chat -> new ChatDto(chat));

        Flux<ChatDto> chatFluxStream = chatRepository.mFindByChatroomIdAndTimestampAfter(id, LocalDateTime.now())
                .map(chat -> new ChatDto(chat))
                .subscribeOn(Schedulers.boundedElastic())
                .doOnError(error -> {
                    log.error("ChatController testGetMessageStreamPaging error : {}", error);
                });

        return recentChats.concatWith(chatFluxStream);
    }

    @GetMapping(value = "/test/paging/{id}/older", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<ChatDto> getOlderMessages(
            @PathVariable(name = "id") Integer id,
            @RequestParam(name = "beforeTimestamp") String beforeTimestamp) {

        log.info("ChatController GET /test/{id}/older API");

        LocalDateTime timestamp = LocalDateTime.parse(beforeTimestamp);

        // Set a limit for the number of older messages to fetch
        PageRequest pageRequest = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "timestamp"));

        return chatRepository.findOlderChatsByChatroomId(id, timestamp, pageRequest)
                .map(ChatDto::new)
                .doOnError(error -> log.error("ChatController getOlderMessages error: {}", error));
    }
}
