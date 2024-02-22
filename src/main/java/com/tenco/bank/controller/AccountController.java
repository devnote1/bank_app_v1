package com.tenco.bank.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.tenco.bank.dto.AccountSaveDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.UnAuthorizedException;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.service.AccountService;

import jakarta.servlet.http.HttpSession;

@Controller // IoC
@RequestMapping("/account")
public class AccountController {

	@Autowired
	private final HttpSession session;
	@Autowired
	private final AccountService accountService;

	// 생성자 의존 주입 - DI 처리
	public AccountController(HttpSession session, AccountService accountService) {
		this.session = session;
		this.accountService = accountService;
	}

	/**
	 * 계좌 목록 페이지
	 * 
	 * @param model - accountList
	 * @return list.jsp
	 */
	@GetMapping({ "/list", "/" })
	public String listPage(Model model) {
		
		// 1.인증 검사가 필요(account 전체 필요)
		User principal = (User) session.getAttribute("principal");
		if (principal == null) {
			throw new UnAuthorizedException("인증된 사용자가 아닙니다", HttpStatus.UNAUTHORIZED);
		}

		// 경우의 수 -> 유, 무
		List<Account> accountList = accountService.readAccountListByUserId(principal.getId());

		if (accountList.isEmpty()) {
			model.addAttribute("accountList", null);
		} else {
			model.addAttribute("accountList", accountList);
		}

		return "account/list";
	}

	// 주소 설계 - http://localhost:8080/account/save
	@GetMapping("/save")
	public String savePage() {
		// 1.인증 검사가 필요(account 전체 필요)
		User principal = (User) session.getAttribute("principal");
		if (principal == null) {
			throw new UnAuthorizedException("인증된 사용자가 아닙니다", HttpStatus.UNAUTHORIZED);
		}
		return "account/save";
	}

	@PostMapping("/save")
	public String saveProc(AccountSaveDTO dto) {
		// 유효성 검사보다 먼저 인증검사를 먼저 하는 것이 좋습니다.

		// 1. 인증검사
		User principal = (User) session.getAttribute("principal");
		if (principal == null) {
			throw new UnAuthorizedException("로그인 먼저 해주세요", HttpStatus.UNAUTHORIZED);
		}

		// 2. 유효성 검사
		if (dto.getNumber() == null || dto.getNumber().isEmpty()) {
			throw new DataDeliveryException("계좌번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}

		if (dto.getPassword() == null || dto.getPassword().isEmpty()) {
			throw new DataDeliveryException("계좌비밀번호를 입력하시오", HttpStatus.BAD_REQUEST);
		}

		if (dto.getBalance() == null || dto.getBalance() <= 0) {
			throw new DataDeliveryException("잘못된 입력 입니다", HttpStatus.BAD_REQUEST);
		}
		accountService.createAccount(dto, principal.getId());

		// TODO 추후 account/list 페이지가 만들어 지면 수정 할 예정 입니다.
		return "redirect:/account/save";
	}

}
