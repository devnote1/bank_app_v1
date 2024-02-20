package com.tenco.bank.controller;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;

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
	
	// todo - 삭제 예정 
	// 주소 설계 
	// http:localhost:8080/error-test1/true
	@GetMapping("error-test1/{isError}")
	public String errorPage(@PathVariable boolean isError) {
		if(isError) {
			throw new RedirectException("잘못된 요청 입니다.", HttpStatus.BAD_REQUEST);
		}
		return "mainPage";
	}
	
	// todo - 삭제 예정 
	// 주소 설계 
	// http:localhost:8080/error-test2/true
	@GetMapping("error-test2/{isError}")
	public String errorData2(@PathVariable boolean isError) {
		if(isError) {
			throw new DataDeliveryException("중복된 이메일을 사용할 수 없습니다", HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return "mainPage";
	}
	
	// todo - 삭제 예정 
	// 주소 설계 
	// http:localhost:8080/error-test3/true
	@GetMapping("error-test3/{isError}")
	public String errorData3(@PathVariable boolean isError) {
		if(isError) {
			throw new DataDeliveryException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}
		return "mainPage";
	}
	
}
