package spring.hugme.domain.magazine.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.hugme.domain.magazine.entity.MagazineCategory;

@Repository
public interface MagazineCategoryRepository extends JpaRepository<MagazineCategory,Integer> {


}
