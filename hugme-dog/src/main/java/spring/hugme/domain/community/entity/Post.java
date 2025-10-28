package spring.hugme.domain.community.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Formula;
import spring.hugme.domain.user.entity.Member;
import spring.hugme.infra.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private int postId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "board_id")
  private Board board;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private Member member;

  private String title;

  private String content;

  @Formula("(SELECT COUNT(*) FROM comments l WHERE l.post_id = id)")
  private int commentCount;

  @Formula("(SELECT COUNT(*) FROM like l WHERE l.post_id = id)")
  private int likeCount;

}
