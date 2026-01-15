package spring.hugme.domain.magazine.model.repo;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.hugme.domain.community.entity.PostImage;
import spring.hugme.domain.magazine.entity.Magazine;
import spring.hugme.domain.magazine.entity.MagazineImage;

public interface MagazineImageRepository extends JpaRepository<MagazineImage, Long> {

  Optional<MagazineImage> findFirstByMagazineAndActivatedTrue(Magazine m);
}
