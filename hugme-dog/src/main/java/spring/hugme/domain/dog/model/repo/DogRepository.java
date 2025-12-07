package spring.hugme.domain.dog.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import spring.hugme.domain.community.entity.Favorite;
import spring.hugme.domain.dog.model.entity.Dog;

@Repository
public interface DogRepository extends JpaRepository<Dog, Long> {

}
