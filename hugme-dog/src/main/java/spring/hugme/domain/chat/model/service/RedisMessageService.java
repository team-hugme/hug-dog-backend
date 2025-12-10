package spring.hugme.domain.chat.model.service;

import java.time.LocalDateTime;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import spring.hugme.domain.chat.dto.LastMessageDto;

@Service
@RequiredArgsConstructor
public class RedisMessageService {

  private final RedisTemplate<String, Object> redisTemplate;

  public void saveLastMessage(Long chatBotId, String content, LocalDateTime createdAt){
    String key = "chatroom:" + chatBotId +":last";
    LastMessageDto dto = new LastMessageDto(content, createdAt.toString());

    redisTemplate.opsForValue().set(key, dto);
  }

  public LastMessageDto getLastMessage(Long chatBotId){
    String key = "chatroom:" + chatBotId +":last";

    return (LastMessageDto) redisTemplate.opsForValue().get(key);
  }

}
