package spring.hugme.domain.community.model.service;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.hugme.domain.community.dto.CommentListResponse;
import spring.hugme.domain.community.entity.Comments;
import spring.hugme.domain.community.entity.Post;
import spring.hugme.domain.community.model.repo.CommentRepository;
import spring.hugme.domain.community.model.repo.PostRepository;
import spring.hugme.global.util.NotFoundException;

@Service
@RequiredArgsConstructor
public class ReactionService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;

  public List<CommentListResponse> CommentView(Long postId) {

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new NotFoundException("해당 게시물 아이디는 존재하지 않습니다."));

    List<Comments> commentsList = commentRepository.findAllByPost(post);

    List<CommentListResponse> commentListResponses= commentsList.stream()
        .map( comment ->
            CommentListResponse.builder()
                .commentId(comment.getCommentId())
                .userId(comment.getMember().getId())
                .name(comment.getMember().getName())
                .content(comment.getContent())
                .createdAt(comment.getCreatedAt())
                .updatedAt(comment.getModifiedAt())
                .build()
        )
        .toList();
    return  commentListResponses;
  }
}
