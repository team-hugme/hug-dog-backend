package spring.hugme.domain.community.model.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.hugme.domain.community.entity.Comments;
import spring.hugme.domain.community.entity.Post;

@Repository
public interface CommentRepository extends JpaRepository<Comments, Long> {

  @Query("SELECT c FROM Comments c JOIN FETCH c.member m WHERE c.post = :post")
  List<Comments> findAllByPost(@Param("post") Post post);
}
