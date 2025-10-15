package spring.hugme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.hugme.model.entity.UserEntity;

import java.util.List;
import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
}

