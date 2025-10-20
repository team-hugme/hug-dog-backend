package spring.hugme.domain.user.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import spring.hugme.domain.user.entity.UserEntity;

import java.util.Optional;


public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
}

