package spring.hugme.domain.community.model.repo.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import spring.hugme.domain.community.entity.Post;
import spring.hugme.domain.community.entity.Comments;
import spring.hugme.domain.community.entity.QComments;
import spring.hugme.domain.user.entity.QMember;

@RequiredArgsConstructor
public class CommentRepositoryImpl implements CommentRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Comments> findAllCommentsWithMemberByPost(Post post) {

    QComments c = QComments.comments;
    QMember m = QMember.member;

    return queryFactory
        .selectFrom(c)
        .join(c.member, m).fetchJoin()
        .where(c.post.eq(post)
            .and(c.activated.eq(true)))
        .fetch();


  }
}
