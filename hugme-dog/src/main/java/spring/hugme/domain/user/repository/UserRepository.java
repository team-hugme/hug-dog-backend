package spring.hugme.domain.user.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import spring.hugme.domain.user.entity.Member;


public interface UserRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByUserId(String userId);
    boolean existsByUserId(String userId);
    boolean existsByEmail(String email);
}

