package jsl.moum.chatappmodule.chat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ReactiveChangeStreamOperation;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.messaging.ChangeStreamRequest;
import org.springframework.data.mongodb.core.query.Criteria;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChatStreamServiceTest {

    @Mock
    private ReactiveMongoTemplate reactiveMongoTemplate = mock(ReactiveMongoTemplate.class);

    @InjectMocks
    private ChatStreamService chatStreamService;

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

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getNewChatStream() {
        // Mock ChangeStreamEvent for the chats
        ChangeStreamEvent<Chat> changeEvent1 = mock(ChangeStreamEvent.class);
        ChangeStreamEvent<Chat> changeEvent2 = mock(ChangeStreamEvent.class);
        ChangeStreamEvent<Chat> changeEvent3 = mock(ChangeStreamEvent.class);

        when(changeEvent1.getBody()).thenReturn(mockChat);
        when(changeEvent2.getBody()).thenReturn(mockChat2);
        when(changeEvent3.getBody()).thenReturn(mockChat3);

        // Mock the reactiveMongoTemplate's changeStream method
        Flux<ChangeStreamEvent<Chat>> mockedFlux = Flux.just(changeEvent1, changeEvent2, changeEvent3);
        ReactiveChangeStreamOperation.ReactiveChangeStream<Chat> reactiveChangeStream = mock(ReactiveChangeStreamOperation.ReactiveChangeStream.class);
        when(reactiveMongoTemplate.changeStream(Chat.class)).thenReturn(reactiveChangeStream);
        when(reactiveChangeStream.watchCollection(Chat.class)).thenReturn(reactiveChangeStream);
        when(reactiveChangeStream.filter(Criteria.where("chatroomId").is(mockChatroom.getId()))).thenReturn(reactiveChangeStream);
        when(reactiveChangeStream.listen()).thenReturn(mockedFlux);

        // Call the service method
        Flux<ChatDto> chatDtoFlux = chatStreamService.getNewChatStream(mockChatroom.getId());

        // Verify the emitted ChatDto objects
        StepVerifier.create(chatDtoFlux)
                .expectNextMatches(chatDto -> {
                    assertEquals("Hello, World!", chatDto.getMessage());
                    assertEquals("kimajou", chatDto.getSender());
                    return true;
                })
                .expectNextMatches(chatDto -> {
                    assertEquals("Second message", chatDto.getMessage());
                    assertEquals("kimajou", chatDto.getSender());
                    return true;
                })
                .expectNextMatches(chatDto -> {
                    assertEquals("Third message", chatDto.getMessage());
                    assertEquals("kimajou", chatDto.getSender());
                    return true;
                })
                .verifyComplete();
    }
}