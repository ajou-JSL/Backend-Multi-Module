package jsl.moum.chatappmodule.chat;

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
    public Mono<ResponseEntity<String>> test(){
        log.info("ChatController GET /test API");
        log.info("ChatController test API");
        return Mono.just(ResponseEntity.ok("ChatController test API"));
    }

}
