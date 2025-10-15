package spring.hugme.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Builder
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Table(name = "member")
public class UserEntity {

    @Id
    private Long id;

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

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "reason_withdraw", length = 300)
    private String reasonWithdraw;

    @CreatedDate
    @Column(name = "create_at", updatable = false)
    private LocalDateTime createAt;

    @LastModifiedDate
    @Column(name = "update_at")
    private LocalDateTime updateAt;

    // ========================
    // 비밀번호 암호화 관련
    // ========================

    public boolean checkPassword(org.springframework.security.crypto.password.PasswordEncoder passwordEncoder, String rawPassword) {
        return passwordEncoder.matches(rawPassword, this.password);
    }
}