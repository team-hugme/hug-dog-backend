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


  @Column(name = "user_id", nullable = false, unique = true)
  private String userId;

  @Column(name = "email", nullable = false, unique = true)
  private String email;

  @Column(name = "password", nullable = false)
  private String password;

  @Column(name = "profile_url", length = 500)
  private String profileUrl;

  @Column(name = "name", nullable = false)
  private String name;

  @Column(name = "birthday")
  private LocalDate birthday;

  @Column(name = "phone", length = 100)
  private String phone;

  @Column(name = "active")
  @Builder.Default
  private Boolean active = true;

  @Column(name = "reason_withdraw", length = 300)
  private String reasonWithdraw;

  @CreatedDate
  @Column(name = "create_at", updatable = false)
  private LocalDateTime createAt;

  @LastModifiedDate
  @Column(name = "update_at")
  private LocalDateTime updateAt;

  // ========================
  // 비밀번호 체크
  // ========================
  public boolean checkPassword(
      org.springframework.security.crypto.password.PasswordEncoder passwordEncoder,
      String rawPassword) {
    return passwordEncoder.matches(rawPassword, this.password);
  }
}
