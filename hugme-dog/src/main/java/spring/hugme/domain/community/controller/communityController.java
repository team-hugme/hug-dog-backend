package spring.hugme.domain.community.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.hugme.domain.community.code.BoardAlias;
import spring.hugme.domain.community.dto.BoardListResponse;
import spring.hugme.domain.community.dto.PostDetailResponse;
import spring.hugme.domain.community.model.service.CommunityService;
import spring.hugme.global.response.CommonApiResponse;
import spring.hugme.global.response.ResponseCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/community/posts")
public class communityController {

  private final CommunityService communityService;

  //전체 조회
  @GetMapping
  public CommonApiResponse<List<BoardListResponse>> BoardAllList(@RequestParam(required = false) BoardAlias type){

    List<BoardListResponse> boardList;

    if (type != null) {
      // 쿼리 파라미터 'type'이 있을 경우 (예: /posts?type=QNA)
      boardList = communityService.BoardTypeAllList(type);
    } else {
      // 쿼리 파라미터 'type'이 없을 경우 (예: /posts)
      boardList = communityService.BoardAllList();
    }

    return CommonApiResponse.success(
        ResponseCode.OK,
        "커뮤니티 조회 성공",
        boardList
        );

  }



  //글 상세
  @GetMapping("/detail/{postId}")
  public CommonApiResponse<PostDetailResponse> PostDetailView(@PathVariable Long postId){

    PostDetailResponse postDetailInfo = communityService.PostDetailView(postId);

    return CommonApiResponse.success(
        ResponseCode.OK,
        "커뮤니티 상세 조회 성공",
        postDetailInfo
    );
  }


}
