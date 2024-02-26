package com.tenco.bank.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;


@Controller 
public class MainController {
	
	@GetMapping({ "/main-page", "/index", "/" })
	public String mainPage() {
		// mainPage.jsp 파일은 도전 과제 입니다. 
		// 여러분들이 원하는 디자인으로 프로젝트  
		// 종료 후에 직접 만들어 보세요 
		return "mainPage";
	}
	
}
