package spring.hugme.domain.community.dto.response;


import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;
import spring.hugme.domain.community.code.BoardAlias;
import spring.hugme.domain.community.dto.TagInfo;

@Data
@Builder
public class PostDetailResponse {
  BoardAlias type;

  UUID userId;

  Long postId;

  Long boarId;

  String nickname;

  List<TagInfo> tag;


  String title;

  String content;

  int likeCount;

  int commentCount;

  LocalDateTime createdAt;

  LocalDateTime updatedAt;
}
