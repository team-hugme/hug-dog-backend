package spring.hugme.domain.community.dto.response;

import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import spring.hugme.global.code.BoardAlias;
import spring.hugme.domain.community.dto.TagInfo;

@Data
@Builder
public class BoardListResponse {
  BoardAlias type;

  UUID userId;

  Long postId;

  Long boarId;

  String nickname;

  List<TagInfo> tag;


  String title;

  String content;

  Long likeCount;

  Long commentCount;


}
