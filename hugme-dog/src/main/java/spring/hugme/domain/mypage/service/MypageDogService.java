package spring.hugme.domain.mypage.service;


import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.hugme.domain.dog.model.entity.Dog;
import spring.hugme.domain.dog.model.repo.DogRepository;
import spring.hugme.domain.mypage.dto.MyDogListResponse;
import spring.hugme.domain.user.entity.Member;
import spring.hugme.domain.user.repository.UserRepository;
import spring.hugme.global.error.exceptions.NotFoundException;

@Service
@RequiredArgsConstructor
public class MypageDogService {

  private final DogRepository dogRepository;
  private final UserRepository userRepository;

  public List<MyDogListResponse> myDogList(String userId) {

    Member member = userRepository.findUserId(userId)
        .orElseThrow(() -> new NotFoundException("해당 유저가 존재하지 않습니다"));


    List<Dog> dogs = dogRepository.findByMember(member);

    return dogs.stream()
        .map(dog ->)

  }
}
