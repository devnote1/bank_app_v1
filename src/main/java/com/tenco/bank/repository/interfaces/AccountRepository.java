package com.tenco.bank.repository.interfaces;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import com.tenco.bank.repository.model.Account;

@Mapper  
public interface AccountRepository {
	
	// 코드 확인 (계좌 생성) 
	public int insert(Account account);
	public int updateById(Account account);
	public int deleteById(Integer id);
	
	// 계좌 조회 - 1 유저 , N 계좌 
	public List<Account> findAllByUserId();
	public Account findByNumber(Integer id);
	
}
