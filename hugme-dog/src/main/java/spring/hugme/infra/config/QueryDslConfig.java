package spring.hugme.infra.config;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class QueryDslConfig {

    @Bean
    public JPAQueryFactory jpaQueryFactory(EntityManager em) {
        // javax → jakarta 불일치 문제 때문에 생성자 직접 호출 대신
        // 실제 QueryDSL 사용 시 JPQLQueryFactory를 직접 주입해서 사용
        return new JPAQueryFactory(() -> em.unwrap(javax.persistence.EntityManager.class));
    }
}

