package spring.hugme.domain.community.model.repo.custom;

import java.util.List;
import spring.hugme.domain.community.dto.PostListProjection;
import spring.hugme.domain.community.entity.Board;
import spring.hugme.domain.community.entity.Post;

public interface PostRepositoryCustom {

  List<Post> findAllByBoardWithBoardAndMember(Board board);

  List<Post> findAllWithAllRelations();

  Post findByPostIdWithAllRelations(Long postId);

  PostListProjection findCountsByPostId(Long postId);

}
