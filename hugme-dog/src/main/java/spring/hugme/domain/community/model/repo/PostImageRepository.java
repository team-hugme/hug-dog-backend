package spring.hugme.domain.community.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.hugme.domain.community.entity.Post;
import spring.hugme.domain.community.entity.PostImage;

@Repository
public interface PostImageRepository extends JpaRepository<PostImage, Long> {

  void deleteAllByPost(Post post);
}
