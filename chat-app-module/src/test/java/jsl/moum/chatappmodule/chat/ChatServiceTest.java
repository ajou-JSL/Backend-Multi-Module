package jsl.moum.chatappmodule.chat;

import jsl.moum.chatappmodule.auth.service.AuthService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
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
import static reactor.core.publisher.Mono.when;

@ExtendWith(MockitoExtension.class)
class ChatServiceTest {

    @InjectMocks
    private ChatService chatService;

    @Mock
    private ChatRepository chatRepository;

    @Mock
    private ChatroomRepository chatroomRepository;

    @Mock
    private Mono<Authentication> authMono;

    @Mock
    private LocalDateTime mockChatTimestamp = LocalDateTime.of(2024, 1, 1, 12, 0, 0, 0);

    private Chat mockChat = Chat.builder()
            .id("0")
            .sender("kimajou")
            .message("Hello, World!")
            .chatroomId(0)
            .timestamp(mockChatTimestamp)
            .build();

    LocalDateTime mockChat2Timestamp = LocalDateTime.of(2024, 1, 1, 12, 2, 0, 0);
    Chat mockChat2 = Chat.builder()
            .id("1")
            .sender("kimajou")
            .message("Second message")
            .chatroomId(0)
            .timestamp(mockChat2Timestamp)
            .build();

    private Chatroom mockChatroom = Chatroom.builder()
            .id(0)
            .name("mockChatroom")
            .type(0)
            .teamId(0)
            .leaderId(0)
            .createdAt(LocalDateTime.of(2020, 1, 1, 12, 0,0, 0))
            .lastChat(mockChat.getMessage())
            .lastTimestamp(mockChat.getTimestamp())
            .fileUrl("mockUrl")
            .build();

    private int PAGE_SIZE = 20;

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
    void buildChat() {
        Chat.Request request = new Chat.Request();
        request.setMessage("Hello, World!");

        StepVerifier.create(authMono)
                .expectNextMatches(authentication -> {
                    Chat chat = chatService.buildChat(request, authentication.getPrincipal().toString(), 0);

                    assertEquals(chat.getSender(), authentication.getPrincipal().toString());
                    assertEquals(chat.getMessage(), "Hello, World!");
                    assertEquals(chat.getChatroomId(), 0);
                    return true; // Ensure the test continues
                })
                .verifyComplete();
    }

    @Test
    void saveChat() {
        // Mock chatroomRepository.findById to return a Mono
        Mockito.when(chatroomRepository.findById(mockChat.getChatroomId()))
                .thenReturn(Mono.just(mockChatroom)); // Replace mockChatroom with a valid mock or test object

        // Mock save behavior for chatroomRepository
        Mockito.when(chatroomRepository.save(mockChatroom))
                .thenReturn(Mono.just(mockChatroom));

        // Mock save behavior for chatRepository
        Mockito.when(chatRepository.save(mockChat))
                .thenReturn(Mono.just(mockChat));

        // Call the service method
        Mono<Chat> savedChat = chatService.saveChat(mockChat);

        StepVerifier.create(savedChat)
                .expectNextMatches(chat -> {
                    assertEquals(mockChat.getMessage(), chat.getMessage());
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void testChatTimestamp() {
        // Verify the timestamp is correctly initialized
        assertEquals(mockChat.getTimestamp(), LocalDateTime.parse("2024-01-01T12:00"));
    }

    @Test
    void getChatsRecentByChatroomId() {
        Mockito.verify(chatRepository, Mockito.never()).save(mockChat); // Example to ensure it's not treated as a mock

        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "timestamp"));

        Mockito.when(chatRepository.findByChatroomId(mockChatroom.getId(), pageRequest))
                .thenReturn(Flux.just(mockChat2, mockChat));

        assertEquals(mockChat.getTimestamp(), LocalDateTime.of(2024, 1, 1, 12, 0, 0, 0));
        assertEquals(mockChat2.getTimestamp(), LocalDateTime.of(2024, 1, 1, 12, 2, 0, 0));

        Flux<ChatDto> result = chatService.getChatsRecentByChatroomId(mockChatroom.getId());

        // StepVerifier to test the emitted ChatDto objects
        StepVerifier.create(result)
                .expectNextMatches(chatDto -> {
                    // Validate the first emitted ChatDto (mockChat2)
                    assertEquals(mockChat.getMessage(), chatDto.getMessage());
                    assertEquals(mockChat.getTimestamp(), LocalDateTime.parse(chatDto.getTimestamp()));
                    return true; // Ensure the test continues
                })
                .expectNextMatches(chatDto -> {
                    // Validate the second emitted ChatDto (mockChat)
                    assertEquals(mockChat2.getMessage(), chatDto.getMessage());
                    assertEquals(mockChat2.getTimestamp(), LocalDateTime.parse(chatDto.getTimestamp()));
                    return true; // Ensure the test continues
                })
                .expectNextCount(0)
                .verifyComplete(); // Ensure the Flux completes successfully
    }

    @Test
    void getChatsBeforeTimestampByChatroomId() {
        // Define the test timestamp
        LocalDateTime testTimestamp = LocalDateTime.of(2024, 1, 1, 12, 1, 0, 0);

        // Define the PageRequests
        PageRequest pageRequest = PageRequest.of(0, PAGE_SIZE, Sort.by(Sort.Direction.DESC, "timestamp"));

        // Mock the repository method to return a Flux of Chats
        Mockito.when(chatRepository.findChatsBeforeTimestampByChatroomId(1, testTimestamp, pageRequest))
                .thenReturn(Flux.just(mockChat));

        // Call the service method
        Flux<ChatDto> result = chatService.getChatsBeforeTimestampByChatroomId(1, testTimestamp);

        // Validate the results using StepVerifier
        StepVerifier.create(result)
                .expectNextMatches(chatDto -> {
                    // Validate the first ChatDto (mockChat1)
                    assertEquals(mockChat.getMessage(), chatDto.getMessage());
                    assertEquals(mockChat.getTimestamp(), LocalDateTime.parse(chatDto.getTimestamp()));
                    return true;
                })
                .expectNextCount(0)
                .verifyComplete();
    }
}