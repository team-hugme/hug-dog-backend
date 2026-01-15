package spring.hugme.domain.magazine.model.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.hugme.domain.community.entity.PostImage;
import spring.hugme.domain.magazine.dto.MagazineSearchResponse;
import spring.hugme.domain.magazine.entity.Magazine;
import spring.hugme.domain.magazine.entity.MagazineCategory;
import spring.hugme.domain.magazine.entity.MagazineImage;
import spring.hugme.domain.magazine.model.repo.MagazineCategoryRepository;
import spring.hugme.domain.magazine.model.repo.MagazineImageRepository;
import spring.hugme.domain.magazine.model.repo.MagazineRepository;
import spring.hugme.global.error.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor
public class MagazineService {

  private final MagazineRepository magazineRepository;
  private final MagazineCategoryRepository magazineCategoryRepository;
  private final MagazineImageRepository magazineImageRepository;

  public List<MagazineSearchResponse> magazineSearch(String search) {


    String searchKeyword = search + "*";

    List<Long> ids = magazineRepository.findIdsByKeyword(searchKeyword);

    if (ids.isEmpty()) {
      return Collections.emptyList();
    }

    List<Magazine> magazines = magazineRepository.findAllWithGraphByIdIn(ids);
    return magazines.stream()
        .map(m -> {
          MagazineCategory myCategory = m.getResource().getMagazineCategory();

          Optional<MagazineImage> imageOpt = magazineImageRepository.findFirstByMagazineAndActivatedTrue(
              m);

          MagazineCategory parentCategory = myCategory.getMagazineCategory();

          String imageUrl = imageOpt.map(MagazineImage::getSavePath)
              .orElse(
                  "https://res.cloudinary.com/dyz2lq1f0/image/upload/v1763707069/post_uploads/m1ryt6ptdy6wyydp4yog.png");

          return MagazineSearchResponse.builder()
              .categoryId(myCategory.getCategoryId())
              .parentId(parentCategory != null ? parentCategory.getCategoryId() : 0)
              .depth(myCategory.getDepth())
              .categoryName(myCategory.getCategoryName())
              .magazineId(m.getMagazineId())
              .resourceId(m.getResource().getResourceId())
              .cancerTitle(m.getCancerTitle())
              .contentSummary(m.getContentSummary())
              .createdAt(m.getCreatedAt())
              .updatedAt(m.getModifiedAt())
              .resourceName(m.getResourceName())
              .resourceURL(m.getResourceURL())
              .imageURL(imageUrl)
              .build();
        }).collect(Collectors.toList());
  }

}
