package jsl.moum.chatappmodule.chat;

import com.mongodb.client.model.changestream.OperationType;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.ChangeStreamEvent;
import org.springframework.data.mongodb.core.ChangeStreamOptions;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import static org.springframework.data.mongodb.core.query.Criteria.where;

@Service
@RequiredArgsConstructor
public class ChatStreamService {

    // ReactiveMongoTemplate is defined as a Spring bean in MongoCollectionConfig.java
    private final ReactiveMongoTemplate reactiveMongoTemplate;

    // Method to pull new chat messages in real-time
    public Flux<ChatDto> getNewChatStream(int chatroomId){
        Flux<ChangeStreamEvent<Chat>> flux = reactiveMongoTemplate.changeStream(Chat.class)
                .watchCollection(Chat.class)
                .filter(where("chatroomId").is(chatroomId))
                .listen();

        return flux
                .mapNotNull(changeStreamEvent -> changeStreamEvent.getBody())
                .map(chat -> new ChatDto(chat));
    }
}