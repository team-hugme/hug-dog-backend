package spring.hugme.domain.chat.dto;

import lombok.Data;

@Data
public class ChatStartRequest {

  //dogId
  Long dogId;

  Long chatBotId;

  // 무지개 다리 건넜는지?
  boolean rainbowTrue;

  //강아지 특징
  String dogFeature;

  //강아지 이름
  String dogName;

  //강아지 나이
  int dogAge;

  //강아지 품종
  String dogBreed;

  //보낸 메세지
  String userMessage;


}
