package spring.hugme.domain.community.model.service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import spring.hugme.domain.community.dto.request.CommentWriteRequest;
import spring.hugme.domain.community.dto.response.CommentListResponse;
import spring.hugme.domain.community.dto.response.CommentWriteResponse;
import spring.hugme.domain.community.entity.Comments;
import spring.hugme.domain.community.entity.Post;
import spring.hugme.domain.community.model.repo.CommentRepository;
import spring.hugme.domain.community.model.repo.PostRepository;
import spring.hugme.domain.user.entity.Member;
import spring.hugme.domain.user.repository.UserRepository;
import spring.hugme.global.util.NotFoundException;

@Service
@RequiredArgsConstructor
public class ReactionService {

  private final CommentRepository commentRepository;
  private final PostRepository postRepository;
  private final UserRepository memberRepository;

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

  @Transactional
  public CommentWriteResponse CommentWrite(CommentWriteRequest commentRequest, Long postId, String userId) {

    Member member = memberRepository.findByUserId(userId)
        .orElseThrow(() -> new EntityNotFoundException("해당 엔티티가 존재하지 않습니다"));

    Post post = postRepository.findById(postId)
        .orElseThrow(() -> new EntityNotFoundException("해당 게시물이 존재하지 않습니다"));

    Comments comments = Comments.builder()
        .member(member)
        .post(post)
        .content(commentRequest.getContent())
        .build();

    comments = commentRepository.save(comments);

    return CommentWriteResponse.builder()
        .postId(postId)
        .content(comments.getContent())
        .name(member.getName())
        .createdAt(comments.getCreatedAt())
        .updatedAt(comments.getModifiedAt())
        .commentCount(post.getCommentCount())
        .build();

  }
}
