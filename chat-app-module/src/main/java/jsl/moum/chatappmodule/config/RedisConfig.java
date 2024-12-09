package jsl.moum.chatappmodule.config;

import jsl.moum.chatappmodule.chat.RedisConnectedUser;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Bean
    ReactiveRedisConnectionFactory redisConnectionFactory(){
        LettuceConnectionFactory connectionFactory = new LettuceConnectionFactory(host, port);
        connectionFactory.afterPropertiesSet();
        return connectionFactory;
    }

    /*
     * ReactiveRedisTemplate is a high-level abstraction for interacting with Redis
     * Note that using ReactiveRedisTemplate offers more functionality (like JPA does)
     * compared to RedisReactiveCommands
     */

    @Bean
    ReactiveRedisTemplate<String, RedisConnectedUser> redisOperations(ReactiveRedisConnectionFactory connectionFactory){
        Jackson2JsonRedisSerializer<RedisConnectedUser> serializer = new Jackson2JsonRedisSerializer<>(RedisConnectedUser.class);

        // Jackson JSON serializers for objects to ensure "ConnectedUser" can be serialized/deserialized properly
        RedisSerializationContext.RedisSerializationContextBuilder<String, RedisConnectedUser> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, RedisConnectedUser> context = builder.value(serializer).build();

        return new ReactiveRedisTemplate<>(connectionFactory, context);
    }

}
