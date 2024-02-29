package com.tenco.bank.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.dto.SigninDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.UserService;
import com.tenco.bank.utils.Define;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user") 
public class UserController {

	@Autowired
	private final UserService userService;
	@Autowired
	private final HttpSession session;

	public UserController(UserService userService, HttpSession session) {
		this.userService = userService;
		this.session = session;
	}

	/**
	 * 회원 가입 페이지 요청
	 * @return signUp.jsp 파일 리턴
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {
		return "user/signUp";
	}

	/**
	 * 회원 가입 요청 처리 
	 * @param SignUpDTO
	 * @return 로그인 페이지 이동
	 */
	@PostMapping("/sign-up")
	public String signProc(SignUpDTO dto) {
		
		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_USERNAME, HttpStatus.BAD_REQUEST);
		}

		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}

		if (dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_FULLNAME, HttpStatus.BAD_REQUEST);
		}
		
		userService.createUser(dto);
	
		return "redirect:/user/sign-in";
	}

	/**
	 * 로그인 화면 요청 
	 */
	@GetMapping("/sign-in")
	public String signInPage() {
		return "user/signin";
	}


	/**
	 * 로그인 처리
	 * 
	 * @param signInFormDto
	 * @return 메인 페이지 이동  
	 * 
	 */
	@PostMapping("/sign-in")
	public String signInProc(SigninDTO dto) {

		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_USERNAME, HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException(Define.ENTER_YOUR_PASSWORD, HttpStatus.BAD_REQUEST);
		}
		User principal = userService.signIn(dto);
		session.setAttribute(Define.PRINCIPAL, principal);
		return "redirect:/main-page";
	}

	/**
	 * 로그 아웃 
	 * @return 로그인 페이지 이동 
	 */
	@GetMapping("/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/user/sign-in";
	}

}