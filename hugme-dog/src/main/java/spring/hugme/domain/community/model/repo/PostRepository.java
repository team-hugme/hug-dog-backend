package spring.hugme.domain.community.model.repo;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import spring.hugme.domain.community.entity.Board;
import spring.hugme.domain.community.entity.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {


  @Query("SELECT p FROM Post p JOIN FETCH p.board b JOIN FETCH p.member WHERE b = :board")
  List<Post> findAllByBoardWithBoardAndMember(Board board);

  @Query("SELECT p FROM Post p JOIN FETCH p.board JOIN FETCH p.member")
  List<Post> findAllWithBoardAndMember();
}
