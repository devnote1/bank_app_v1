package com.tenco.bank.repository.interfaces;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.bank.dto.HistoryAccountDTO;
import com.tenco.bank.repository.model.Account;

@Mapper  
public interface AccountRepository {
	
	public int insert(Account account);
	public int updateById(Account account);
	public int deleteById(Integer id); 
	public List<Account> findAllByUserId(@Param("userId") Integer principalId);
	public Account findByNumber(@Param("number")String id);
	// 코드 추가 부분 
	public Account findByAccountId(Integer accountId);

}
