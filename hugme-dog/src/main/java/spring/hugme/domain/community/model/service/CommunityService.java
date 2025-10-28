package spring.hugme.domain.community.model.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.hugme.domain.community.code.BoardAlias;
import spring.hugme.domain.community.dto.BoardListResponse;
import spring.hugme.domain.community.entity.Board;
import spring.hugme.domain.community.entity.Post;
import spring.hugme.domain.community.model.repo.BoardRepository;
import spring.hugme.domain.community.model.repo.PostRepository;
import spring.hugme.global.response.CommonApiResponse;
import spring.hugme.global.response.ResponseCode;
import spring.hugme.global.util.NotFoundException;

@Service
@RequiredArgsConstructor
public class CommunityService {

  private final PostRepository postRepository;
  private final BoardRepository boardRepository;

  public List<BoardListResponse> BoardAllList() {

    List<Post> postList = postRepository.findAllWithBoardAndMember();

    List<BoardListResponse> boardList = postList.stream()
        .map(post ->
            BoardListResponse.builder()
                .boarId(post.getBoard().getBoardId())
                .userId(post.getMember().getId())
                .title(post.getTitle())
                .content(post.getContent())
                .commentCount(post.getCommentCount())
                .type(post.getBoard().getType())
                .likeCount(post.getLikeCount())
                .build()
        )
        .collect(Collectors.toList());
    return  boardList;

  }


  public List<BoardListResponse> BoardTypeAllList(BoardAlias type) {

    Board board = boardRepository.findByType(type)
        .orElseThrow(() ->new NotFoundException("커뮤니티 타입 요청이 잘못되었습니다"));

    List<Post> postList = postRepository.findAllByBoardWithBoardAndMember(board);

    List<BoardListResponse> boardList = postList.stream()
        .map(post ->
            BoardListResponse.builder()
                .boarId(post.getBoard().getBoardId())
                .userId(post.getMember().getId())
                .title(post.getTitle())
                .content(post.getContent())
                .commentCount(post.getCommentCount())
                .type(post.getBoard().getType())
                .likeCount(post.getLikeCount())
                .build()
        )
        .collect(Collectors.toList());
    return  boardList;

  }
}
