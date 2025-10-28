package spring.hugme.domain.community.dto;

import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import spring.hugme.domain.community.code.BoardAlias;

@Data
@Builder
public class BoardListResponse {
  BoardAlias type;

  UUID userId;

  Long postId;

  Long boarId;

  String title;

  String content;

  int likeCount;

  int commentCount;


}
