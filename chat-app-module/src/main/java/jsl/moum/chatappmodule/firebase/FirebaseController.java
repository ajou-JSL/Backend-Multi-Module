package jsl.moum.chatappmodule.firebase;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/api/firebase")
public class FirebaseController {

    private final FirebaseService firebaseService;

    @PostMapping("/send/test")
    public Mono<String> sendNotification(){
        String token = "TEST_TOKEN";
        String title = "TEST_TITLE";
        String body = "TEST_BODY";
        return firebaseService.sendNotification(token, title, body);
    }


}
