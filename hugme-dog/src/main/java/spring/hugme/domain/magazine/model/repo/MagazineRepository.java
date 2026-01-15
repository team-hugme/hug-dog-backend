package spring.hugme.domain.magazine.model.repo;

import java.util.List;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import spring.hugme.domain.magazine.entity.Magazine;
import spring.hugme.domain.magazine.model.repo.custom.MagazineRepositoryICustom;

@Repository
public interface MagazineRepository extends JpaRepository<Magazine, Long>, MagazineRepositoryICustom {


  @Query(value = """
            SELECT m.magazine_id 
            FROM magazine m 
            WHERE MATCH(m.cancer_title, m.content_translation) 
            AGAINST(:keyword IN BOOLEAN MODE)
            """, nativeQuery = true)
  List<Long> findIdsByKeyword(@Param("keyword") String keyword);


  @EntityGraph(attributePaths = {
      "resource",
      "resource.magazineCategory",
      "resource.magazineCategory.magazineCategory"
  })
  @Query("SELECT m FROM Magazine m WHERE m.magazineId IN :ids")
  List<Magazine> findAllWithGraphByIdIn(@Param("ids") List<Long> ids);
}
