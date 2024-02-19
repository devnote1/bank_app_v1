package com.tenco.bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller // IoC 대상(싱글톤으로 관리 됨)
public class MainController {

	// 주소 설계 하기
	// http:localhost:8080/main-page
	@GetMapping({ "main-page", "/index", "/" })
	public String mainPage() {
		System.out.println("동기적 방식으로 동작 --> mainPage() 호출 확인");
		// [[ JSP 파일 찾기 - yml 파일 설정 부분 ]]
		// prefix: /WEB-INF/view/
		// mainPage <- (String 값으로 직접 작성)
		// suffix: .jsp
		return "mainPage";
	}
}
