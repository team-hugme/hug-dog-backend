package spring.hugme.domain.community.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.hugme.domain.community.code.BoardAlias;
import spring.hugme.domain.community.dto.BoardListResponse;
import spring.hugme.domain.community.model.service.CommunityService;
import spring.hugme.global.controller.BaseController;
import spring.hugme.global.response.CommonApiResponse;
import spring.hugme.global.response.ResponseCode;

@RestController
@RequiredArgsConstructor
@RequestMapping("/community")
public class communityController extends BaseController {

  private final CommunityService communityService;

  //전체 조회
  @GetMapping("/posts")
  public CommonApiResponse<List<BoardListResponse>> BoardAllList(){

    List<BoardListResponse> boardList = communityService.BoardAllList();


    return CommonApiResponse.success(
        ResponseCode.OK,
        boardList
        );

  }

  //주제별 전체 글 목록
  @GetMapping("/posts")
  public CommonApiResponse<List<BoardListResponse>> BoardTypeAllList(@RequestParam BoardAlias type){

    List<BoardListResponse> boardTypeList = communityService.BoardTypeAllList(type);

    return CommonApiResponse.success(
        ResponseCode.OK,
        boardTypeList
    );

  }

  //글 상세

}
