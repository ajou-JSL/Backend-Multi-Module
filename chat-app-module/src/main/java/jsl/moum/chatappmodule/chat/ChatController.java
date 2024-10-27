package jsl.moum.chatappmodule.chat;

import jsl.moum.chatappmodule.global.response.ResponseCode;
import jsl.moum.chatappmodule.global.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@RequiredArgsConstructor
@RestController // 데이터 리턴 서버
@RequestMapping("/api/chat")
public class ChatController {

    @GetMapping("/test")
    public Mono<ResponseEntity<?>> test(){
        log.info("ChatController GET /test API");
        log.info("ChatController test API");

        Mono<ResultResponse> resultResponse = Mono.just(ResultResponse.of(ResponseCode.TEST_SUCCESS, "ChatController test API"));
        return Mono.just(ResponseEntity.ok().body(resultResponse));
    }

}
