package spring.hugme.domain.community.model.service;

import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.hugme.domain.community.dto.PostListProjection;
import spring.hugme.global.code.BoardAlias;
import spring.hugme.domain.community.dto.BoardListResponse;
import spring.hugme.domain.community.dto.PostDetailResponse;
import spring.hugme.domain.community.dto.TagInfo;
import spring.hugme.domain.community.entity.Board;
import spring.hugme.domain.community.entity.Post;
import spring.hugme.domain.community.entity.PostHashtag;
import spring.hugme.domain.community.model.repo.BoardRepository;
import spring.hugme.domain.community.model.repo.PostHashTagRepository;
import spring.hugme.domain.community.model.repo.PostRepository;
import spring.hugme.global.util.NotFoundException;

@Service
@RequiredArgsConstructor
public class CommunityService {

  private final PostRepository postRepository;
  private final BoardRepository boardRepository;
  private final PostHashTagRepository postHashTagRepository;

// 전체 글 보기
  public List<BoardListResponse> BoardAllList() {

    List<Post> postList = postRepository.findAllWithAllRelations();

    return toBoardListResponse(postList);
  }


  //해당되는 타입 글 보기
  public List<BoardListResponse> BoardTypeAllList(BoardAlias type) {

    Board board = boardRepository.findByType(type)
        .orElseThrow(() -> new NotFoundException("커뮤니티 타입 요청이 잘못되었습니다"));

    List<Post> postList = postRepository.findAllByBoardWithBoardAndMember(board);


    return toBoardListResponse(postList);
  }

  public PostDetailResponse PostDetailView(Long postId) {

    Post post = postRepository.findByPostIdWithAllRelations(postId);

    List<PostHashtag> postHashtags = postHashTagRepository.findAllByPost(post);


    List<TagInfo> tagInfos = postHashtags.stream()
        .map(tag ->
            TagInfo.builder()
                .tagId(tag.getHashtagId())
                .tagName(tag.getHashtagContent())
                .build()
        )
        .toList();

    PostListProjection counts = postRepository.findCountsByPostId(postId);

    PostDetailResponse postDetailResponse = PostDetailResponse.builder()
        .userId(post.getMember().getId())
        .postId(postId)
        .boarId(post.getBoard().getBoardId())
        .type(post.getBoard().getType())
        .nickname(post.getMember().getName())
        .tag(tagInfos)
        .title(post.getTitle())
        .content(post.getContent())
        .commentCount(counts.getCommentCount())
        .likeCount(counts.getLikeCount())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getModifiedAt())
        .build();

    return postDetailResponse;


  }

  private List<BoardListResponse> toBoardListResponse(List<Post> postList) {
    return postList.stream()
        .map(post -> {

          List<TagInfo> tagInfoList = post.getHashtagList().stream()
              .map(hashtag -> TagInfo.builder()
                  .tagId(hashtag.getHashtagId())
                  .tagName(hashtag.getHashtagContent())
                  .build())
              .collect(Collectors.toList());

          PostListProjection counts = postRepository.findCountsByPostId(post.getPostId());

          return BoardListResponse.builder()
              .boarId(post.getBoard().getBoardId())
              .userId(post.getMember().getId())
              .nickname(post.getMember().getName())
              .postId(post.getPostId())
              .title(post.getTitle())
              .content(post.getContent())
              .commentCount(counts.getCommentCount())
              .type(post.getBoard().getType())
              .likeCount(counts.getLikeCount())
              .tag(tagInfoList)
              .build();
        })
        .collect(Collectors.toList());
  }
}
