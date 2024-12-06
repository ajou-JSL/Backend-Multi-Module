package jsl.moum.chatappmodule.chat;

import jsl.moum.chatappmodule.auth.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ChatControllerTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private Mono<Authentication> authMono;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = new SecurityContextImpl(new TestingAuthenticationToken("kimajou", "Password!1", "ROLE_USER"));
        ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext));

        // Bind the SecurityContext to the reactive pipeline
        authMono = Mono.deferContextual(ctx -> ReactiveSecurityContextHolder.getContext()
                        .map(SecurityContext::getAuthentication))
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }

    @AfterEach
    void tearDown() {
        ReactiveSecurityContextHolder.clearContext();
    }

    @Test
    void testWithMockSecurityContext() {
        StepVerifier.create(authMono)
                .expectNextMatches(authentication -> {
                    assertEquals("kimajou", authentication.getPrincipal().toString());
                    return true; // Ensure the test continues
                })
                .verifyComplete();
    }

    @Test
    void sendMessage() {
        StepVerifier.create(
                        authMono.flatMap(authentication -> {
                            String sender = authentication.getPrincipal().toString();
                            assertEquals("kimajou", sender);

                            Chat.Request request = new Chat.Request();
                            request.setMessage("Hello, World!");
                            Chat chat = chatService.buildChat(request, sender, 0);

                            // Assertions for chat object
                            assertEquals("Hello, World!", chat.getMessage());
                            assertEquals("kimajou", chat.getSender());

                            return Mono.just(chat); // Return the Chat object for further validation if needed
                        })
                )
                .expectNextMatches(chat -> {
                    // Verify the chat object here
                    assertEquals("Hello, World!", chat.getMessage());
                    assertEquals("kimajou", chat.getSender());
                    assertNotEquals("notkimajou", chat.getSender());
                    return true; // Ensure the test continues
                })
                .verifyComplete();
    }

    @Test
    void getChatStream() {
        Chat.Request request = new Chat.Request();
        request.setMessage("message1");
        Chat chat1 = chatService.buildChat(request, "sender1", 0);

        request.setMessage("message2");
        Chat chat2 = chatService.buildChat(request, "sender2", 0);

        request.setMessage("message3");
        Chat chat3 = chatService.buildChat(request, "sender3", 0);

        Flux<ChatDto> recentChats = Flux.just(new ChatDto(chat1), new ChatDto(chat2), new ChatDto(chat3));

        StepVerifier.create(recentChats)
                .expectNextMatches(chatDto -> {
                    assertEquals("message1", chatDto.getMessage());
                    return true; // Ensure the test continues
                })
                .expectNextMatches(chatDto -> {
                    assertEquals("message2", chatDto.getMessage());
                    return true; // Ensure the test continues
                })
                .expectNextMatches(chatDto -> {
                    assertEquals("message3", chatDto.getMessage());
                    return true; // Ensure the test continues
                })
                .verifyComplete();
    }

    @Test
    void getChatsBeforeTimestamp() {
        Chat.Request request = new Chat.Request();
        request.setMessage("message1");
        Chat chat1 = chatService.buildChat(request, "sender1", 0);
        chat1.setTimestamp(LocalDateTime.parse("2010-09-01T00:00:00"));

        request.setMessage("message2");
        Chat chat2 = chatService.buildChat(request, "sender2", 0);
        chat2.setTimestamp(LocalDateTime.parse("2023-09-01T00:00:00"));

        request.setMessage("message3");
        Chat chat3 = chatService.buildChat(request, "sender3", 0);
        chat3.setTimestamp(LocalDateTime.parse("2012-09-01T00:00:00"));

        Flux<Chat> recentChats = Flux.just(chat1, chat2, chat3);

        StepVerifier.create(recentChats.filter(chat -> chat.getTimestamp().isBefore(LocalDateTime.parse("2023-09-01T00:00:00"))))
                .expectNextMatches(chatDto -> {
                    assertEquals("message1", chatDto.getMessage());
                    return true; // Ensure the test continues
                })
                .expectNextMatches(chatDto -> {
                    assertEquals("message3", chatDto.getMessage());
                    return true; // Ensure the test continues
                })
                .expectNextCount(0)
                .verifyComplete();
    }
}