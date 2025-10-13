package src.main.java.spring.hugme.infra;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;


@Configuration
@EntityScan("src.main.java.spring")
@EnableJpaRepositories("src.main.java.spring")
@EnableTransactionManagement
public class DomainConfig {
}
