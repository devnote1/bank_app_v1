package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.service.UserService;

@Controller
@RequestMapping("/user") // 대문 처리 
public class UserController {
	
	// 1단계 
	@Autowired // DI 처리 
	private UserService userService;
	// 2단계 
	//private final UserService userService;
	
	// 2단계
	// private final UserService userService
	// final 처리를 하는 이유는 (성능향상)
	// @Autowired 함께 표시 가능, 표시 하는 이유는 (가독성 향상)
	public UserController(UserService userService) {
		this.userService = userService;
	}
	
	/**
	 * 회원 가입 페이지 요청 
	 * 주소 설계 http://localhost:8080/user/sign-up
	 * @return signUp.jsp 파일 리턴
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {
	   //   prefix: /WEB-INF/view/
	   //   suffix: .jsp
		return "user/signUp";
	}
	
	
	// 회원 가입 요청 처리 
	// 주소 설계 http://localhost:8800/user/sign-up
	// Get, Post -> sign-up 같은 도메인이라도 구분이 가능하다. 
	// REST API 를 사용하는 이유에 대해한번 더 살펴 보세요  
	@PostMapping("/sign-up")
	public String signProc(SignUpDTO dto) {
		
		// 1. 인증검사 x 
		// 2. 유효성 검사 
		if(dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException("username을 입력 하세요", 
					HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("password을 입력 하세요", 
					HttpStatus.BAD_REQUEST);
		}
		
		if(dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new DataDeliveryException("fullname을 입력 하세요", 
					HttpStatus.BAD_REQUEST);
		}		
		userService.createUser(dto);
		
		// todo 로그인 페이지로 변경 예정
		return "redirect:/user/sign-up"; 
	}
	
	
}