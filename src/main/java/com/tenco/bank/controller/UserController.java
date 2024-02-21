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

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user") // 대문 처리
public class UserController {

	@Autowired
	private final UserService userService;
	// 코드 추가
	// 세션 메모리지에 접근하기 위한 HttpSession 을 선언 해주세요
	@Autowired
	private final HttpSession session;

	// 코드 수정 - DI 처리
	public UserController(UserService userService, HttpSession session) {
		this.userService = userService;
		this.session = session;
	}

	/**
	 * 회원 가입 페이지 요청 주소 설계 http://localhost:8080/user/sign-up
	 * 
	 * @return signUp.jsp 파일 리턴
	 */
	@GetMapping("/sign-up")
	public String signUpPage() {
		// prefix: /WEB-INF/view/
		// suffix: .jsp
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
		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException("username을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("password을 입력 하세요", HttpStatus.BAD_REQUEST);
		}

		if (dto.getFullname() == null || dto.getFullname().isEmpty()) {
			throw new DataDeliveryException("fullname을 입력 하세요", HttpStatus.BAD_REQUEST);
		}
		userService.createUser(dto);

		// todo 로그인 페이지로 변경 예정
		return "redirect:/user/sign-up";
	}

	/**
	 * 로그인 화면 요청 주소 설계 http://localhost:8080/user/sign-in
	 */
	@GetMapping("/sign-in")
	public String signInPage() {
		// 인증 검사가 불필요 하다.
		// prefix: /WEB-INF/view/
		// suffix: .jsp
		return "user/signin";
	}

	// 코드 추가
	/**
	 * 로그인 처리
	 * 
	 * @param signInFormDto
	 * @return 메인 페이지 이동 (수정 예정) 생각해보기 GET 방식 처리는 브라우저 히스토리에 남겨지기 때문에 예외적으로 로그인 POST
	 *         방식으로 처리 한다. (보안)
	 */
	@PostMapping("/sign-in")
	public String signInProc(SigninDTO dto) {
		// 1. 유효성 검사
		if (dto.getUsername() == null || dto.getUsername().isEmpty()) {
			throw new DataDeliveryException("username을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("password을 입력하시오", HttpStatus.BAD_REQUEST);
		}
		User principal = userService.signIn(dto);

		// 세션메모리에 등록 처리
		session.setAttribute("principal", principal);

		// 처리 후에 새로운 request 를 생성할 수 있도록 리다이렉트 처리 합니다.
		// TODO - 추후 계좌 목록 페이로 변경해 주세요
		// return "redirect:/account/list";
		return "redirect:/main-page";
	}

	// 로그아웃 처리 
	@GetMapping("/logout")
	public String logout() {
		session.invalidate();
		return "redirect:/user/sign-in";
	}

}