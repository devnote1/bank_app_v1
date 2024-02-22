package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.AccountSaveDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.model.Account;

@Service
public class AccountService {

	@Autowired
	private final AccountRepository accountRepository;

	public AccountService(AccountRepository accountRepository) {
		this.accountRepository = accountRepository;
	}

	/**
	 * 계좌 생성 기능
	 * 
	 * @param dto
	 * @param pricipalId 
	 */
	@Transactional
	public void createAccount(AccountSaveDTO dto, Integer pricipalId) {
		try {
			accountRepository.insert(dto.toAccount(pricipalId));
		} catch (DataAccessException e) {
			// DB연결 및 제약 사항 위한 및 쿼리 오류 
			throw new DataDeliveryException("잘못된 처리 입니다", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			// 예외 처리 - 에러 페이지로 이동
			throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	/**
	 * 복잡한 Select 쿼리문일 경우 트랜잭션 처리를 해주 것이 좋습니다.  
	 * 여기서는 단순한 Select 구문이라 바로 진행 합니다. 
	 * @param principalId
	 * @return
	 */
	public List<Account> readAccountListByUserId(Integer principalId) {
		List<Account> accountListEntity = null;
		try {
			accountListEntity = accountRepository.findAllByUserId(principalId);
		} catch (DataAccessException e) {
			throw new DataDeliveryException("잘못된 처리 입니다", HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			// 예외 처리 - 에러 페이지로 이동
			throw new RedirectException("알 수 없는 오류", HttpStatus.SERVICE_UNAVAILABLE);
		}
		return accountListEntity;
	}

}
