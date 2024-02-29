package com.tenco.bank.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tenco.bank.dto.SignUpDTO;
import com.tenco.bank.dto.SigninDTO;
import com.tenco.bank.handler.exception.DataDeliveryException;
import com.tenco.bank.handler.exception.RedirectException;
import com.tenco.bank.repository.interfaces.UserRepository;
import com.tenco.bank.repository.model.User;
import com.tenco.bank.utils.Define;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {
	 
	@Autowired // DI 처리 
	private final PasswordEncoder passwordEncoder;
	@Autowired
	private final UserRepository userRepository;

	
	/**
	 * 회원 생성 서비스  
	 * @param SignUpDTO
	 */
	public void createUser(SignUpDTO dto) {
		int result = 0; 
		try {
			 
			// 회원가입 요청자기 제출한 password 부분을 암화호 처리
			String hashPwd = passwordEncoder.encode(dto.getPassword());
			dto.setPassword(hashPwd);
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
	
	// 사용자 이름만으로 정보 조회
	public User readUserByUsername(String username) {
		return userRepository.findByUsername(username);
	}
	
	/**
	 * 로그인 서비스 
	 * @param SigninDTO
	 * @return userEntity or null 
	 */
	public User signIn(SigninDTO dto) {
		User userEntity = null; 
		try {
			userEntity = userRepository.findByUsername(dto.getUsername());
		} catch (DataAccessException e) {
			throw new DataDeliveryException(Define.INVALID_INPUT,HttpStatus.INTERNAL_SERVER_ERROR);
		} catch (Exception e) {
			throw new RedirectException(Define.UNKNOWN , HttpStatus.SERVICE_UNAVAILABLE);
		}

		if(userEntity == null) {
			throw new DataDeliveryException("아이디를 확인해주세요",
					HttpStatus.BAD_REQUEST);
		}
		
		boolean isPwdMatched = passwordEncoder.matches(dto.getPassword(), 
														userEntity.getPassword());
		if(isPwdMatched == false) {
			throw new DataDeliveryException("비밀번호가 잘못되었습니다.",
										HttpStatus.BAD_REQUEST);
		}
		return userEntity;
	}

}
