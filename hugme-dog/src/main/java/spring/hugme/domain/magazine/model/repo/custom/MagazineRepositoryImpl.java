package spring.hugme.domain.magazine.model.repo.custom;

import com.querydsl.jpa.impl.JPAQueryFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import spring.hugme.domain.magazine.entity.Magazine;
import spring.hugme.domain.magazine.entity.QMagazine;
import spring.hugme.domain.magazine.entity.QResource;

@RequiredArgsConstructor
public class MagazineRepositoryImpl implements MagazineRepositoryICustom {

  private final JPAQueryFactory queryFactory;
}
