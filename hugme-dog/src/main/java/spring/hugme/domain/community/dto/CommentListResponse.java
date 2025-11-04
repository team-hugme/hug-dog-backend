package spring.hugme.domain.community.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentListResponse {

  Long commentId;

  UUID userId;

  String name;

  String content;

  LocalDateTime createdAt;

  LocalDateTime updatedAt;

}
