package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.AccountSaveDTO;
import com.tenco.bank.dto.DepositDto;
import com.tenco.bank.dto.WithdrawalDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.AccountRepository;
import com.tenco.bank.repository.interfaces.HistoryRepository;
import com.tenco.bank.repository.model.Account;
import com.tenco.bank.repository.model.History;
import com.tenco.bank.utils.Define;

import lombok.RequiredArgsConstructor;

@Service
//final 멤버 변수 사용시 직정 생성자를 만들기 않고 @RequiredArgsConstructor 대체 가능 합니다 
@RequiredArgsConstructor  
public class AccountService {

	@Autowired
	private final AccountRepository accountRepository;
	@Autowired
	private final HistoryRepository historyRepository;

// @RequiredArgsConstructor 대체 
//	public AccountService(AccountRepository accountRepository) {
//		this.accountRepository = accountRepository;
//	}

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
			throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
	
	/**
	 * 사용자별 계좌 번호 조회 서비스 
	 * @param principalId
	 * @return List<Account> or Null 
	 */
	public List<Account> readAccountListByUserId(Integer principalId) {
		List<Account> accountListEntity = null;
		try {
			accountListEntity = accountRepository.findAllByUserId(principalId);
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.INVALID_INPUT, HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
		return accountListEntity;
	}
	
	// 한번에 모든 기능을 생각하는 것은 힘든 부분 입니다. 
	// 주석을 활용해서 하나씩 만들어야 하는 기능을 먼저 정의하고 
	// 코드를 작성해 봅시다. 
	// 출금 기능 만들기 
	// 1. 계좌 존재 여부 확인 -- select 
	// 2. 본인 계좌 여부 확인 -- 객체에서 확인 처리 
	// 3. 계좌 비번 확인 
	// 4. 잔액 여부 화인 
	// 5. 출금 처리 ---> update 
	// 6. 거래 내역 등록 --> insert(history) 
	// 7. 트랜잭션 처리 
	@Transactional
	public void updateAccountWithdraw(WithdrawalDTO dto, Integer principalId) {
		// 1
		Account accountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		if(accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// 2. 1단계 직접 코드 작성, 2단계 Entity에 메서드를 만들어 두기 
		accountEntity.checkOwner(principalId);
		// 3.
		accountEntity.checkPassword(dto.getWAccountPassword());
		// 4.
		accountEntity.checkBalacne(dto.getAmount());
		
		// 5 --> 출금 기능 (Account) --> 객체 상태값 변경  
		accountEntity.withdraw(dto.getAmount());
		
		accountRepository.updateById(accountEntity);
		// 6 --> 거래 내역 등록 
		History history = new History();
		history.setAmount(dto.getAmount());
		history.setWBalance(accountEntity.getBalance()); 
		history.setDBalance(null);
		history.setWAccountId(accountEntity.getId());
		history.setDAccountId(null);
		
	    int rowResultCount = historyRepository.insert(history);
	    if(rowResultCount != 1) {
	    	throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	}
	
	// 입금 기능 만들기
	// 1. 계좌 존재여부 확인(select) 
	// 2. 계좌 존재 -> 본인 계좌 여부 확인(객체)
	// 3. 입금 처리 -> update 
	// 4. 거래 내역 등록 - insert 
	// 5. 트랜잭션 처리
	@Transactional
	public void updateAccountDeposit(DepositDto dto, Integer principalId) {

		// 1. 계좌 존재 여부 확인
		Account accountEntity = accountRepository.findByNumber(dto.getDAccountNumber());
		if (accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// 2. 본인 계좌 여부 확인
		accountEntity.checkOwner(principalId);

		// 3. 입금처리(객체 상태 변경 후 update 처리)  
		accountEntity.deposit(dto.getAmount());
		accountRepository.updateById(accountEntity);

		// 4. history에 거래내역 등록
		History history = new History();
		history.setAmount(dto.getAmount());
		history.setWAccountId(null);
		history.setDAccountId(accountEntity.getId());
		history.setWBalance(null); 
		history.setDBalance(accountEntity.getBalance());

		int rowResultCount = historyRepository.insert(history);
		if (rowResultCount != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}
}
