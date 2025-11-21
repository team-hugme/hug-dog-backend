package spring.hugme.domain.community.model.repo.custom;

import spring.hugme.domain.community.entity.Comments;
import spring.hugme.domain.community.entity.Post;
import java.util.List;

public interface CommentRepositoryCustom {

  List<Comments> findAllCommentsWithMemberByPost(Post post);

}
