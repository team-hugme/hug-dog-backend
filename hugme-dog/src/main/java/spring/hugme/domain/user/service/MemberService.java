package spring.hugme.domain.user.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.hugme.domain.user.entity.Member;
import spring.hugme.domain.user.repository.UserRepository;
import spring.hugme.global.util.NotFoundException;

@Service
@RequiredArgsConstructor
public class MemberService {

  private final UserRepository userRepository;

  public Member findByUserId(String userId){
    return userRepository.findByUserId(userId).orElseThrow(() -> new NotFoundException("해당 유저가 존재 하지 않습니다"));
  }

}
