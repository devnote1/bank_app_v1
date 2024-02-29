package com.tenco.bank.service;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
	@Transactional
	public void createUser(SignUpDTO dto) {
		int result = 0; 
		  
		if (!dto.getMFile().isEmpty()) {
			String[] fileNames = uploadFile(dto.getMFile());
			// dto 객체 상태 변경 
			dto.setOriginFileName(fileNames[0]);
			dto.setUploadFileName(fileNames[1]);
	    }
		
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
	
	/**
	 * 
	 * @param file
	 * MultipartFile getOriginalFilename : 사용자가 작성한 파일 명 
	 * uploadFileName : 서버 컴퓨터에 저장 될 파일 명 
	 * @return index 0, 1 
	 */
	private String[] uploadFile(MultipartFile file) {
	    if (file.getSize() > Define.MAX_FILE_SIZE) {
	        throw new DataDeliveryException("파일 크기는 20MB 이상 클 수 없습니다", HttpStatus.BAD_REQUEST);
	    }
	    	
	    // 서버 컴퓨터에 파일 넣을 디렉토리가 있는지 검사 
	    String saveDirectory = Define.UPLOAD_FILE_DERECTORY;  
	    File directory = new File(saveDirectory);
	    // 폴더가 없다면 생성 처리 
	    if (!directory.exists()) {
	        directory.mkdirs();
	    }
	    
	    // 파일 이름 (중복 처리 예방) 
	    String uploadFileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
	    String uploadPath = saveDirectory + File.separator + uploadFileName;
	    File destination = new File(uploadPath);

	    try {
	        file.transferTo(destination);
	    } catch (IllegalStateException | IOException e) {
	        throw new DataDeliveryException("파일 업로드 중 오류가 발생했습니다", HttpStatus.INTERNAL_SERVER_ERROR);
	    }
	    
	    return new String[] {file.getOriginalFilename(), uploadFileName};
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
