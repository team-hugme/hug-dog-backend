package spring.hugme.domain.chat.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class ChatResponse {

  Long chatBotId;

  ChatMessageResponse userMessage;

  ChatMessageResponse assistantMessage;

}
