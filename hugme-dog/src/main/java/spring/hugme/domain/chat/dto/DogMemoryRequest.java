package spring.hugme.domain.chat.dto;

import java.util.List;
import lombok.Data;

@Data

public class DogMemoryRequest {

  List<String> chatLogs;

  Long dogId;

}
