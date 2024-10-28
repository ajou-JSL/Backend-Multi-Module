package jsl.moum.chatappmodule.firebase;

import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class FirebaseService {

    public Mono<String> sendNotification(String token, String title, String body){
        Message message = Message.builder()
                .setToken(token)
                .setNotification(Notification.builder()
                        .setTitle(title)
                        .setBody(body)
                        .build())
                .build();

        return Mono.fromCallable(() -> FirebaseMessaging.getInstance().send(message))
                .doOnSuccess(response -> log.info("Successfully sent message: {} | {}", response, message))
                .doOnError(error -> log.info("Error sending message: {}",error.getMessage()));
    }

}
