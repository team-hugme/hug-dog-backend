package spring.hugme.global.controller;

import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 모든 Controller 의 공통 prefix 를 정의하는 상위 클래스
 */
@RequestMapping("/api/v1/*")
public abstract class BaseController {

}
