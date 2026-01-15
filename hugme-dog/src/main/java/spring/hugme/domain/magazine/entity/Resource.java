package spring.hugme.domain.magazine.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import spring.hugme.infra.entity.BaseEntity;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//하나의 매거진에 여러개의 리소스가 들어갈수 있고 하나의 리소스는 여러개의 매거진이 참조 될수 있다
//ManyToMany이므로 중간다리 역할할 하나의 엔티티가 더 필요하다 !!
public class Resource extends BaseEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long resourceId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "category_id")
  private MagazineCategory magazineCategory;

  private String categoryTitle;

  private String CancerTitle;

  private String sectionHeader;

  private String subtitle;

  private String detailTitle;

  private String detailContent;

  private String ResourceURL;

}
