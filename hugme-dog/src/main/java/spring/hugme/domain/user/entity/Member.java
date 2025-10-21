package spring.hugme.domain.user.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Member {

  @Id
  @GeneratedValue
  @JdbcTypeCode(SqlTypes.BINARY)
  @Column(columnDefinition = "BINARY(16)", updatable = false, nullable = false)
  private UUID id;


  private String userId;

  private String email;

  private String password;

  private String profileUrl;

  private String name;

  private LocalDate birthday;

  private String phone;

  private String reasonWithdRaw;


  // ========================
  // 비밀번호 체크
  // ========================
  public boolean checkPassword(
      org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
      String rawPassword) {
    return passwordEncoder.matches(rawPassword, this.password);
  }
}
