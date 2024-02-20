package com.tenco.bank.handler.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

// 404.. 에러 페이지동 이동 시킬 때 사용하는 예외 클래스  
@Getter
public class RedirectException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	
	private HttpStatus status;

	public RedirectException(String message, HttpStatus status) {
		super(message);
		this.status = status;
	}
	
}
