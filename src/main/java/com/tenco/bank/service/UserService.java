package com.tenco.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.UserRepository;

@Service // IoC 대상(싱글톤으로 해당 객체를 관리) 
public class UserService {

	@Autowired
	private final UserRepository userRepository;

	// 생성자 의존 주입 DI
	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	// 회원 가입 처리 
	// 예외 처리 
	// DB 에서 연결이나 쿼리 실행, 제약 사항 위한 같은
	// 예외는 RuntimeException 으로 예외를 잡을 수 없습니다. 
	@Transactional // 트랜 잭션 처리 습관
	public void createUser(SignUpDTO dto) {
		// Http 응답으로 클라이언트에게 전달할 오류 메시지는 최소한으로 유지하고,
		// 보안 및 사용자 경험 측면에서 민감한 정보를 노출하지 않도록 합니다.
		try {
			// 데이터베이스 작업
			int result = userRepository.insert(dto.toUser());
			if (result != 1) {
				// 삽입된 행의 수가 1이 아닌 경우 예외 발생
				throw new DataDeliveryException("회원 가입 실패", HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} catch (DataAccessException e) {
			// DataAccessException는 Spring의 데이터 액세스 예외 클래스로,
			// 데이터베이스 연결이나 쿼리 실행과 관련된 문제를 처리합니다.
			throw new DataDeliveryException("잘못된 처리 입니다",HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			// 그 외 예외 처리 - 페이지 이동 처리 
			throw new RedirectException("알 수 없는 오류" , HttpStatus.SERVICE_UNAVAILABLE);
		}
	}

}
