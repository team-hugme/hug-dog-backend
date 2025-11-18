package spring.hugme.domain.community.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import spring.hugme.domain.community.dto.request.CommentWriteRequest;
import spring.hugme.domain.community.dto.response.CommentListResponse;
import spring.hugme.domain.community.dto.response.CommentWriteResponse;
import spring.hugme.domain.community.model.service.ReactionService;
import spring.hugme.global.controller.BaseController;
import spring.hugme.global.response.CommonApiResponse;
import spring.hugme.global.response.ResponseCode;

@RestController
@RequiredArgsConstructor
@RequestMapping(BaseController.API_V1 + "/community/posts", produces = MediaType.APPLICATION_JSON_VALUE)
@Slf4j
public class ReactionController {

  private final ReactionService reactionService;

  //댓글 목록
  @GetMapping("{postId}/comment")
  public CommonApiResponse<List<CommentListResponse>> CommentByPostIdList(@PathVariable Long postId){

    List<CommentListResponse> commentListResponse = reactionService.CommentView(postId);

    return CommonApiResponse.success(
        ResponseCode.OK,
        "정상적으로 댓글 목록이 성공적으로 불러와졌습니다.",
        commentListResponse

    );

  }

  //댓글 작성
  @PostMapping("/{postId}/comment")
  public CommonApiResponse<CommentWriteResponse> CommentWrite(@RequestBody final CommentWriteRequest commentRequest, @AuthenticationPrincipal String userId, @PathVariable Long postId){

    log.info(commentRequest.getContent());
    CommentWriteResponse response = reactionService.CommentWrite(commentRequest, postId, userId);


    return CommonApiResponse.success(
        ResponseCode.CREATED,
        "정상적으로 커뮤니티 댓글이 생성되었습니다",
        response
    );

  }


}
