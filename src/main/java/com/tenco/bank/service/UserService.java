package com.tenco.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.dto.SigninDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.UserRepository;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.utils.Define;

@Service  
public class UserService {

	@Autowired
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}
	
	/**
	 * 회원 생성 서비스  
	 * @param SignUpDTO
	 */
	public void createUser(SignUpDTO dto) {
		int result = 0; 
		try {
			result = userRepository.insert(dto.toUser());
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.INVALID_INPUT,HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN, HttpStatus.SERVICE_UNAVAILABLE);
		}
		
		if (result != 1) {
			throw new DataDeliveryException(Define.FAIL_TO_CREATE_USER, HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	/**
	 * 로그인 서비스 
	 * @param SigninDTO
	 * @return userEntity or null 
	 */
	public User signIn(SigninDTO dto) {
		User userEntity = null; 
		try {
			userEntity = userRepository.findByUsernameAndPassword(dto.getUsername(), dto.getPassword());
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.INVALID_INPUT,HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN , HttpStatus.SERVICE_UNAVAILABLE);
		}
		// todo - 추후 수정 예정 
		// username, password 분리 예정 
		if(userEntity == null) {
			throw new DataDeliveryException("아이디 혹은 비번이 틀렸습니다",
					HttpStatus.BAD_REQUEST);
		}
		return userEntity;
	}

}
