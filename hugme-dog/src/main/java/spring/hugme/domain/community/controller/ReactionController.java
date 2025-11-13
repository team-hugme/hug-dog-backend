package spring.hugme.domain.community.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.hugme.domain.community.dto.CommentListResponse;
import spring.hugme.domain.community.model.service.ReactionService;
import spring.hugme.global.response.CommonApiResponse;
import spring.hugme.global.response.ResponseCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/posts")
public class ReactionController {

  private final ReactionService reactionService;

  //댓글 목록
  @GetMapping("{postId}/comment")
  public CommonApiResponse<List<CommentListResponse>> CommentByPostIdList(@PathVariable Long postId){

    List<CommentListResponse> commentListResponse = reactionService.CommentView(postId);

    return CommonApiResponse.success(
        ResponseCode.OK,
        "댓글 목록이 성공적으로 불러와졌습니다.",
        commentListResponse

    );

  }


}
