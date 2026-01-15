package spring.hugme.domain.magazine.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import spring.hugme.domain.magazine.dto.MagazineSearchResponse;
import spring.hugme.domain.magazine.model.service.MagazineService;
import spring.hugme.global.controller.BaseController;
import spring.hugme.global.response.CommonApiResponse;
import spring.hugme.global.response.ResponseCode;

@RequestMapping(value = BaseController.API_V1 + "/magazine", produces = MediaType.APPLICATION_JSON_VALUE)
@RestController
@RequiredArgsConstructor
public class MagazineController {


  private final MagazineService magazineService;

  @GetMapping("/search")
  public CommonApiResponse<List<MagazineSearchResponse>> magazineSearch(@RequestParam String search){

    List<MagazineSearchResponse> response = magazineService.magazineSearch(search);

    return CommonApiResponse.success(
        ResponseCode.OK,
        "정상적으로 검색한 정보가 요청되었습니다.",
        response
    );
  }

}
