package spring.hugme.domain.community.dto.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import spring.hugme.domain.community.code.BoardAlias;

@Data
@NoArgsConstructor
public class CommentWriteRequest {

  private String content;

}
