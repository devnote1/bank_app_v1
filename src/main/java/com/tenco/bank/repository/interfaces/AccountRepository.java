package com.tenco.bank.repository.interfaces;

import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.tenco.bank.repository.model.Account;

@Mapper  
public interface AccountRepository {
	
	// 코드 확인 (계좌 생성) 
	public int insert(Account account);
	public int updateById(Account account);
	public int deleteById(Integer id);
	
	// 계좌 조회 - 1 유저 , N 계좌
	// interface 파라미터명과 xml에 사용할 변수명을 다르게 해야 된다면 @Param 
	// 어노테이션을 활용할 수 있습니다. 
	// 2개 이상에 파라미터을 설계한다면 반드시 @Param 어노테이션을 지정해 주세요 
	public List<Account> findAllByUserId(@Param("userId") Integer principalId);
	public Account findByNumber(Integer id);
	
}
