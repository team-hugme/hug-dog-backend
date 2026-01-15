package spring.hugme.domain.magazine.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MagazineSearchResponse {

  private int categoryId;

  private int parentId;

  private int depth;

  private String categoryName;

  private Long magazineId;

  private Long resourceId;

  private String cancerTitle;

  private String contentSummary;

  private LocalDateTime createdAt;

  private LocalDateTime updatedAt;

  private String resourceName;

  private String resourceURL;

  private String imageURL;


}
