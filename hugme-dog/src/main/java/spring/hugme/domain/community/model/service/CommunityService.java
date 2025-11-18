package spring.hugme.domain.community.model.service;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.io.File;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import spring.hugme.domain.community.code.BoardAlias;
import spring.hugme.domain.community.dto.response.BoardListResponse;
import spring.hugme.domain.community.dto.response.PostDetailResponse;
import spring.hugme.domain.community.dto.request.PostWriteRequest;
import spring.hugme.domain.community.dto.response.PostWriteResponse;
import spring.hugme.domain.community.dto.TagInfo;
import spring.hugme.domain.community.entity.Board;
import spring.hugme.domain.community.entity.Post;
import spring.hugme.domain.community.entity.PostHashtag;
import spring.hugme.domain.community.entity.PostImage;
import spring.hugme.domain.community.model.repo.BoardRepository;
import spring.hugme.domain.community.model.repo.PostHashTagRepository;
import spring.hugme.domain.community.model.repo.PostImageRepository;
import spring.hugme.domain.community.model.repo.PostRepository;
import spring.hugme.domain.user.entity.Member;
import spring.hugme.global.util.NotFoundException;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommunityService {

  private final PostRepository postRepository;
  private final BoardRepository boardRepository;
  private final PostHashTagRepository postHashTagRepository;
  private final PostImageRepository postImageRepository;

  @Value("${upload.path}")
  private String uploadPath;

  @PostConstruct
  public void init() {
    // src/main/resources/static/image 디렉토리 사용
    String projectRoot = System.getProperty("user.dir");
    this.uploadPath = projectRoot + File.separator + "src" + File.separator + "main" +
        File.separator + "resources" + File.separator + "static" +
        File.separator + "images";
    File uploadDir = new File(uploadPath);
    if (!uploadDir.exists()) {
      uploadDir.mkdirs();
    }
    log.info("File upload path initialized to: {}", uploadPath);
  }


  public List<BoardListResponse> BoardAllList () {


      List<Post> postList = postRepository.findAllWithAllRelations();

      List<BoardListResponse> boardList = postList.stream()
          .map(post -> {

            List<PostHashtag> postHashtags = post.getHashtagList();

            List<TagInfo> tagInfoList = postHashtags.stream()
                .map(hashtag -> TagInfo.builder()
                    .tagId(hashtag.getHashtagId())
                    .tagName(hashtag.getHashtagContent())
                    .build())
                .collect(Collectors.toList());

            return BoardListResponse.builder()
                .boarId(post.getBoard().getBoardId())
                .userId(post.getMember().getId())
                .nickname(post.getMember().getName())
                .postId(post.getPostId())
                .title(post.getTitle())
                .content(post.getContent())
                .commentCount(post.getCommentCount())
                .type(post.getBoard().getType())
                .likeCount(post.getLikeCount())
                .tag(tagInfoList)

                .build();
          })
          .collect(Collectors.toList());

      return boardList;

  }

  public List<BoardListResponse> BoardTypeAllList(BoardAlias type) {

    Board board = boardRepository.findByType(type)
        .orElseThrow(() ->new NotFoundException("커뮤니티 타입 요청이 잘못되었습니다"));

    List<Post> postList = postRepository.findAllByBoardWithBoardAndMember(board);

    List<BoardListResponse> boardList = postList.stream()
        .map(post -> {

          List<PostHashtag> postHashtags = post.getHashtagList();

          List<TagInfo> tagInfoList = postHashtags.stream()
              .map(hashtag -> TagInfo.builder()
                  .tagId(hashtag.getHashtagId())
                  .tagName(hashtag.getHashtagContent())
                  .build())
              .collect(Collectors.toList());

          return BoardListResponse.builder()
              .boarId(post.getBoard().getBoardId())
              .userId(post.getMember().getId())
              .title(post.getTitle())
              .postId(post.getPostId())
              .content(post.getContent())
              .commentCount(post.getCommentCount())
              .type(post.getBoard().getType())
              .likeCount(post.getLikeCount())
              .tag(tagInfoList)
              .build();
        })
        .collect(Collectors.toList());

    return  boardList;

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

    PostDetailResponse postDetailResponse = PostDetailResponse.builder()
        .userId(post.getMember().getId())
        .postId(postId)
        .boarId(post.getBoard().getBoardId())
        .type(post.getBoard().getType())
        .nickname(post.getMember().getName())
        .tag(tagInfos)
        .title(post.getTitle())
        .content(post.getContent())
        .commentCount(post.getCommentCount())
        .likeCount(post.getLikeCount())
        .createdAt(post.getCreatedAt())
        .updatedAt(post.getModifiedAt())
        .build();

    return postDetailResponse;


  }

  @Transactional
  public PostWriteResponse PostWrite(Member member, PostWriteRequest request) {

    Board board = boardRepository.findByType(request.getType())
        .orElseThrow(() -> new NotFoundException("해당 게시판이 존재하지 않습니다"));

    Post post = Post.builder()
        .board(board)
        .member(member)
        .title(request.getTitle())
        .content(request.getContent())
        .build();

    postRepository.save(post);

    if(request.getHashTagContent() != null){
      for(String postHashTag : request.getHashTagContent()){
        PostHashtag postHashtag = PostHashtag.builder()
            .post(post)
            .hashtagContent(postHashTag)
            .build();
        postHashTagRepository.save(postHashtag);
      }

    }

    for (String imagePath : request.getImageURL()) {
      PostImage image = PostImage.builder()
          .post(post)
          .originFileName(extractFileName(imagePath))
          .renameFileName(extractFileName(imagePath))
          .savePath(imagePath)
          .build();
      postImageRepository.save(image);
    }
    PostWriteResponse response = PostWriteResponse.builder()
        .postId(post.getPostId())
        .boardId(board.getBoardId())
        .type(board.getType())
        .build();

    return response;

  }

  private String extractFileName(String path) {
    return path.substring(path.lastIndexOf('/') + 1);
  }

  public void PostModify(PostWriteRequest request, String userId, Long postId) {

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new NotFoundException("해당 게시물이 존재하지 않습니다"));

    post.setContent(request.getContent());
    post.setTitle(request.getTitle());

    if(request.getHashTagContent() != null){
      for(String postHashTag : request.getHashTagContent()){
        PostHashtag postHashtag = PostHashtag.builder()
            .post(post)
            .hashtagContent(postHashTag)
            .build();
        postHashTagRepository.save(postHashtag);
      }

    }

    for (String imagePath : request.getImageURL()) {
      PostImage image = PostImage.builder()
          .post(post)
          .originFileName(extractFileName(imagePath))
          .renameFileName(extractFileName(imagePath))
          .savePath(imagePath)
          .build();
      postImageRepository.save(image);
    }



  }
}
