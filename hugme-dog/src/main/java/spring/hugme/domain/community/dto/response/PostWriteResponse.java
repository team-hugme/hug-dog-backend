package spring.hugme.domain.community.dto.response;


import lombok.Builder;
import lombok.Data;
import spring.hugme.domain.community.code.BoardAlias;

@Data
@Builder
public class PostWriteResponse {

  Long postId;

  Long boardId;

  BoardAlias type;

}
