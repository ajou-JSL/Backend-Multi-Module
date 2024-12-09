package jsl.moum.chatappmodule.chat;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class RedisService {

    private final ReactiveRedisTemplate<String, RedisConnectedUser> redisTemplate;

    /*
    * A Redis set is an unordered collection of unique strings (members)
    * ":" is used as coding convention when using Redis
     */

    // Save a ConnectedUser to Redis
    // This method saves a ConnectedUser in Redis under the key room:<roomNum>.
    // It stores the ConnectedUser as part of a Redis Set.
    public Mono<Boolean> saveConnectedUser(RedisConnectedUser redisConnectedUser) {
        String key = "room:" + redisConnectedUser.getRoomNum();
        return redisTemplate.opsForSet()
                .add(key, redisConnectedUser)
                .map(result -> (Boolean) (result > 0))
                .doFirst(() -> log.info("RedisService saveConnectedUser key : {} | {}", key, redisConnectedUser))
//                .doOnNext(value -> log.info("RedisService saveConnectedUser result : {} | {}", key, value))
                .doOnError(error -> log.error("RedisService saveConnectedUser error : {}", error));
    }

    // Get all users connected to a specific room
    // This method returns the number of users connected to a specific room.
    // It uses the Redis SCARD command via ReactiveRedisTemplate.
    public Mono<Long> getConnectedUserCount(Long roomNum) {
        log.info("RedisService getConnectedUserCount roomNum : {}", roomNum);
        String key = "room:" + roomNum;
        return redisTemplate.opsForSet().size(key)
                .doOnNext(count -> log.info("RedisService getConnectedUserCount count : {}", count))
                .doOnError(error -> log.error("RedisService getConnectedUserCount error : {}", error));

    }

    public Flux<RedisConnectedUser> getConnectedUsers(Long roomNum) {
        log.info("RedisService getConnectedUsers roomNum : {}", roomNum);
        String key = "room:" + roomNum;
//        return redisTemplate.opsForSet().members(key)
//                .doOnNext(connectedUsers -> log.info("RedisService getConnectedUsers connectedUsers : {}", connectedUsers));
        return redisTemplate
                .listenToChannel(key)
                .map(message -> message.getMessage())
                .doOnNext(redisConnectedUsers -> log.info("RedisService getConnectedUsers connectedUsers : {} | {}", key, redisConnectedUsers));
    }


    // Remove a user from a room
    // This method removes a user from the Redis set representing the room they were connected to.
    public Mono<Long> removeConnectedUser(RedisConnectedUser redisConnectedUser) {
        String key = "room:" + redisConnectedUser.getRoomNum();
        return redisTemplate.opsForSet().remove(key, redisConnectedUser)
                .doOnNext(result -> log.info("RedisService removeConnectedUser result : {}", result))
                .doOnError(error -> log.error("RedisService removeConnectedUser error : {}", error));
    }

    // Check if a user is connected in a room
    // This method checks if a specific user is connected to the room.
    // It uses the Redis SISMEMBER command via ReactiveRedisTemplate.
    public Mono<Boolean> isUserConnected(String userId, Long roomNum) {
        String key = "room:" + roomNum;
        return redisTemplate.opsForSet()
                .isMember(key, new RedisConnectedUser(userId, roomNum))
                .doOnNext(result -> log.info("RedisService isUserConnected result : {}", result));
    }

    // Remove all users from a room
    // This method deletes the entire room key from Redis, effectively removing all users from the room.
    public Mono<Boolean> removeAllUsersFromRoom(Long roomNum) {
        String key = "room:" + roomNum;
        return redisTemplate.delete(key).map(deleted -> (Boolean) (deleted > 0));
    }

}
