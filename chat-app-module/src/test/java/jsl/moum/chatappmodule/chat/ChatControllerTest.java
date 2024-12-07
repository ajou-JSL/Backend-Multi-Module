package jsl.moum.chatappmodule.chat;

import jsl.moum.chatappmodule.TestSecurityConfig;
import jsl.moum.chatappmodule.auth.domain.CustomReactiveUserDetailsService;
import jsl.moum.chatappmodule.auth.jwt.JwtReactiveFilter;
import jsl.moum.chatappmodule.auth.jwt.JwtUtil;
import jsl.moum.chatappmodule.auth.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@WebFluxTest(ChatController.class)
@Import(TestSecurityConfig.class)
class ChatControllerTest {

    @MockBean
    private JwtReactiveFilter jwtReactiveFilter;

    @MockBean
    private JwtUtil jwtUtil;

    @MockBean
    private AuthService authService;

    @MockBean
    private ChatService chatService;

    @MockBean
    private ChatStreamService chatStreamService;

    @Mock
    private Mono<Authentication> mockAuthMono;

    @InjectMocks
    private ChatController chatController;

    private LocalDateTime mockChatTimestamp = LocalDateTime.of(2024, 1, 1, 12, 0, 0, 0);
    private Chat mockChat = Chat.builder()
            .id("0")
            .sender("kimajou")
            .message("Hello, World!")
            .chatroomId(0)
            .timestamp(mockChatTimestamp)
            .build();

    private LocalDateTime mockChat2Timestamp = LocalDateTime.of(2024, 1, 1, 12, 2, 0, 0);
    private Chat mockChat2 = Chat.builder()
            .id("1")
            .sender("kimajou")
            .message("Second message")
            .chatroomId(0)
            .timestamp(mockChat2Timestamp)
            .build();

        private LocalDateTime mockChat3Timestamp = LocalDateTime.of(2024, 1, 1, 12, 4, 0, 0);
        private Chat mockChat3 = Chat.builder()
                .id("2")
                .sender("kimajou")
                .message("Third message")
                .chatroomId(0)
                .timestamp(mockChat3Timestamp)
                .build();

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = new SecurityContextImpl(new TestingAuthenticationToken("kimajou", "Password!1", "ROLE_USER"));
        ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext));

        // Bind the SecurityContext to the reactive pipeline
        mockAuthMono = Mono.deferContextual(ctx -> ReactiveSecurityContextHolder.getContext()
                        .map(SecurityContext::getAuthentication))
                .contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }

    @AfterEach
    void tearDown() {
        ReactiveSecurityContextHolder.clearContext();
    }

    @Test
    void testWithMockSecurityContext() {
        StepVerifier.create(mockAuthMono)
                .expectNextMatches(authentication -> {
                    assertEquals("kimajou", authentication.getPrincipal().toString());
                    return true; // Ensure the test continues
                })
                .verifyComplete();
    }

    @Test
    void sendMessage() {

        Chat.Request request = new Chat.Request();
        request.setMessage("Hello, World!");

        Mockito.when(authService.getAuthentication()).thenReturn(mockAuthMono);
        Mockito.when(chatService.buildChat(request, mockChat.getSender(), mockChat.getChatroomId())).thenReturn(mockChat);
        Mockito.when(chatService.saveChat(mockChat)).thenReturn(Mono.just(mockChat));

        Mono<Authentication> authMono = authService.getAuthentication();

        StepVerifier.create(
                        authMono.flatMap(authentication -> {
                            String sender = authentication.getPrincipal().toString();
                            assertEquals("kimajou", sender);

                            Chat chat = chatService.buildChat(request, sender, mockChat.getChatroomId());

                            // Assertions for chat object
                            assertEquals("Hello, World!", chat.getMessage());
                            assertEquals("kimajou", chat.getSender());

                            return chatService.saveChat(chat); // Return the Chat object for further validation if needed
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
        Mockito.when(chatService.getChatsRecentByChatroomId(mockChat.getChatroomId()))
                .thenReturn(Flux.just(new ChatDto(mockChat)));

        Mockito.when(chatStreamService.getNewChatStream(mockChat2.getChatroomId()))
                .thenReturn(Flux.create(emitter -> {
                    emitter.next(new ChatDto(mockChat2));
                    emitter.next(new ChatDto(mockChat3));
                    emitter.complete();
                }));

        Flux<ChatDto> chatFluxStream = chatService.getChatsRecentByChatroomId(mockChat.getChatroomId())
                        .concatWith(chatStreamService.getNewChatStream(mockChat2.getChatroomId()));

        StepVerifier.create(chatFluxStream)
                .expectNextMatches(chatDto -> {
                    assertEquals("Hello, World!", chatDto.getMessage());
                    return true;
                })
                .expectNextMatches(chatDto -> {
                    assertEquals("Second message", chatDto.getMessage());
                    return true;
                })
                .expectNextMatches(chatDto -> {
                    assertEquals("Third message", chatDto.getMessage());
                    return true;
                })
                .expectComplete()
                .verify();

        Mockito.verify(chatService).getChatsRecentByChatroomId(0);
        Mockito.verify(chatStreamService).getNewChatStream(0);
    }


    @Test
    void getChatsBeforeTimestamp() {

        LocalDateTime testTimestamp = LocalDateTime.of(2024, 1, 1, 12, 3, 0, 0);

        Mockito.when(chatService.getChatsBeforeTimestampByChatroomId(Mockito.anyInt(), Mockito.any()))
                .thenReturn(Flux.just(new ChatDto(mockChat), new ChatDto(mockChat2)));

        Flux<ChatDto> chatFluxStream = chatService.getChatsBeforeTimestampByChatroomId(mockChat.getChatroomId(), testTimestamp);

        StepVerifier.create(chatFluxStream)
                .expectNextMatches(chatDto -> {
                    assertEquals("Hello, World!", chatDto.getMessage());
                    return true;
                })
                .expectNextMatches(chatDto -> {
                    assertEquals("Second message", chatDto.getMessage());
                    return true;
                })
                .expectComplete()
                .verify();

        // Verify interactions with the service
        Mockito.verify(chatService).getChatsBeforeTimestampByChatroomId(mockChat.getChatroomId(), testTimestamp);
    }
}