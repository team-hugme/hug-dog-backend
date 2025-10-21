package spring.hugme.infra.entity;


import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
@Setter
@Getter

public class BaseEntity {

  protected Boolean activated = true;

  @CreatedDate
  protected LocalDateTime createdAt = LocalDateTime.now();
  @LastModifiedDate
  protected LocalDateTime modifiedAt = LocalDateTime.now();

  public BaseEntity(Boolean activated) {
    this.activated = activated;
  }

  public void unActivated(){
    this.activated = false;
  }

  public BaseEntity() {
  }

  public LocalDateTime getCreatedAt(){
    return createdAt;}

  public Boolean getActivated() {
    return activated;
  }
}
