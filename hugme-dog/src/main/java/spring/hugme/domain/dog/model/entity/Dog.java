package spring.hugme.domain.dog.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.time.LocalDate;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.hugme.domain.user.entity.Member;
import spring.hugme.global.code.DogGender;
import spring.hugme.global.code.DogSize;
import spring.hugme.infra.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Dog extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long dogId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id")
  private Member member;


  private String dogName;

  @Enumerated(EnumType.STRING)
  private DogGender gender;

  private boolean isNeuered;

  private String breed;

  private LocalDateTime birth;

  private boolean disease;

  private String imageURL;

  @Enumerated(EnumType.STRING)
  private DogSize dogSize;

}
