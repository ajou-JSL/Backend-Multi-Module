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
//        Mono<ErrorResponse> errorResult = Mono.just(ErrorResponse.of(ErrorCode.CHAT_SEND_FAILED));
//        return Mono.just(new ResponseEntity<>(result, HttpStatus.OK));
        return result
                .map(res -> new ResponseEntity<>(res, HttpStatus.OK)) // map success response
                .doOnError(error -> {
                    log.error("ChatController testSendMessage error : {}", error);
                })
                .onErrorReturn(new ResponseEntity<>(ResultResponse.of(ResponseCode.CHAT_SEND_FAILED, null), HttpStatus.BAD_REQUEST));
    }

    @GetMapping(value = "/test/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<Chat> testGetMessageStream(@PathVariable(name = "id") Integer id){
        log.info("ChatController GET /test/{id} API");

        return chatRepository.mFindByChatroomId(id)
                .subscribeOn(Schedulers.boundedElastic());
    }
}
