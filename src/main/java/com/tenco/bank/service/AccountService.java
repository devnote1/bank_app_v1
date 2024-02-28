package com.tenco.bank.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.AccountSaveDTO;
import com.tenco.bank.dto.DepositDto;
import com.tenco.bank.dto.HistoryAccountDTO;
import com.tenco.bank.dto.TransferDTO;
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
@RequiredArgsConstructor
public class AccountService {

	@Autowired
	private final AccountRepository accountRepository;
	@Autowired
	private final HistoryRepository historyRepository;

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
	 * 
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
		if (accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		// 2. 1단계 직접 코드 작성, 2단계 Entity에 메서드를 만들어 두기
		accountEntity.checkOwner(principalId);
		// 3.
		accountEntity.checkPassword(dto.getWAccountPassword());
		// 4.
		accountEntity.checkBalance(dto.getAmount());
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
		if (rowResultCount != 1) {
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

	// 이체 기능만들기
	// 1. 출금 계좌 존재 여부
	// 2. 입금 계좌 존재 확인
	// 3. 출금 계좌 본인 소유 확인
	// 4. 출금 계좌 비번 확인
	// 5. 출금 계좌 잔액 확인
	// 6. 출금 계좌 잔액 객체 수정
	// 7. 입금 계좌 잔액 객체 수정
	// 8. 출금 계좌 update
	// 9. 입금 계좌 update
	// 10. 거래 내역 등록 처리
	// 11.트랜잭션 처리
	@Transactional
	public void updateAccountTransfer(TransferDTO dto, Integer principalId) {
		// 출금 계좌 정보 조회
		Account withdrawAccountEntity = accountRepository.findByNumber(dto.getWAccountNumber());
		// 입금 계좌 정보 조회
		Account depositAccountEntity = accountRepository.findByNumber(dto.getDAccountNumber());

		if (withdrawAccountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		if (depositAccountEntity == null) {
			// 하드 코딩된 문자열을 리팩토링 대상입니다. 추후 직접 만들어서 수정해보세요
			throw new DataDeliveryException("상대방의 계좌 번호가 없습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
		}

		withdrawAccountEntity.checkOwner(principalId);
		withdrawAccountEntity.checkPassword(dto.getPassword());
		withdrawAccountEntity.checkBalance(dto.getAmount());
		withdrawAccountEntity.withdraw(dto.getAmount());
		depositAccountEntity.deposit(dto.getAmount());

		int resultRowCountWithdraw = accountRepository.updateById(withdrawAccountEntity);
		int resultRowCountDeposit = accountRepository.updateById(depositAccountEntity);

		if (resultRowCountWithdraw != 1 && resultRowCountDeposit != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}

		// TransferDTO 에 History 객체를 반환하는 메서들 만들어 줄 수 있습니다.
		// 여기서는 직접 만들도록 하겠습니다.
		History history = History.builder().amount(dto.getAmount()) // 이체 금액
				.wAccountId(withdrawAccountEntity.getId()) // 출금 계좌
				.dAccountId(depositAccountEntity.getId()) // 입금 계좌
				.wBalance(withdrawAccountEntity.getBalance()) // 출금 계좌 남은 잔액
				.dBalance(depositAccountEntity.getBalance()) // 입금 계좌 남은 잔액
				.build();

		int resultRowCountHistory = historyRepository.insert(history);
		if (resultRowCountHistory != 1) {
			throw new DataDeliveryException(Define.FAILED_PROCESSING, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	// 단일 계좌 조회 기능 
	public Account readAccountById(Integer accountId) {
		Account accountEntity = accountRepository.findByAccountId(accountId);
		if (accountEntity == null) {
			throw new DataDeliveryException(Define.NOT_EXIST_ACCOUNT, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return accountEntity;
	}
	
	/**
	 * 단일 계좌 거래 내역 조회  
	 * @param type = [all, deposit, withdrawal]
	 * @param account_id (account_tb PK) 
	 * @return 입금, 출금, 입출금 거래내역 (3가지 타입) OR null   
	 */
	@Transactional // 복잡한 Select 구문에는 트랜잭션 처리하는 것이 좋다
	// ACID - 트랜잭션 처리를 위해 지켜야 할 네 가지 핵심 특성
	// 고립성(Isolation) 
	// 트랜잭션이 독립적으로 실행되고, 다른 트랜잭션의 연산이 현재 트랜잭션에
	// 영향을 주지 않도록 보장하는 성질을 의미
	public List<HistoryAccountDTO> readHistoryByAccountId(String type, Integer accountId) {
		List<HistoryAccountDTO> historyDtos = historyRepository.findByAccountIdAndTypeOfHistory(type, accountId);
		return historyDtos; 
	}
}
