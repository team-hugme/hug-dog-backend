package spring.hugme.domain.community.model.repo.custom;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import spring.hugme.domain.community.dto.PostDetailResponse;
import spring.hugme.domain.community.dto.PostListProjection;
import spring.hugme.domain.community.entity.Board;
import spring.hugme.domain.community.entity.Post;
import spring.hugme.domain.community.entity.QBoard;
import spring.hugme.domain.community.entity.QComments;
import spring.hugme.domain.community.entity.QFavorite;
import spring.hugme.domain.community.entity.QPost;
import spring.hugme.domain.community.entity.QPostHashtag;
import spring.hugme.domain.user.entity.QMember;

@RequiredArgsConstructor
public class PostRepositoryImpl implements PostRepositoryCustom{

  private final JPAQueryFactory queryFactory;

  @Override
  public List<Post> findAllByBoardWithBoardAndMember(Board board) {
    QBoard b = QBoard.board;
    QPost p = QPost.post;

    return queryFactory.selectFrom(p)
        .join(p.board, b).fetchJoin()
        .join(p.member).fetchJoin()
        .leftJoin(p.hashtagList).fetchJoin()
        .where(p.board.eq(board))
        .fetch();
  }

  @Override
  public List<Post> findAllWithAllRelations() {
    QPost p = QPost.post;

    return queryFactory.selectFrom(p)
        .join(p.board).fetchJoin()
        .join(p.member).fetchJoin()
        .leftJoin(p.hashtagList).fetchJoin()
        .fetch();
  }

  @Override
  public Post findByPostIdWithAllRelations(Long postId) {
    QPost p = QPost.post;

    return queryFactory.selectFrom(p)
        .join(p.board).fetchJoin()
        .join(p.member).fetchJoin()
        .leftJoin(p.hashtagList).fetchJoin()
        .where(p.postId.eq(postId))
        .fetchOne();
  }


  @Override
  public PostListProjection findCountsByPostId(Long postId) {
    QPost p = QPost.post;
    QComments c = QComments.comments;
    QFavorite f = QFavorite.favorite;

    return queryFactory
        .select(
            Projections.constructor(PostListProjection.class,
                p.postId,

                queryFactory.select(c.count())
                    .from(c)
                    .where(c.post.eq(p)),

                queryFactory.select(f.count())
                    .from(f)
                    .where(f.post.eq(p))
            )
        )
        .from(p)
        .where(p.postId.eq(postId)) // 특정 게시글만 조회
        .fetchOne();
  }

}
