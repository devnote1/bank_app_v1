package com.tenco.bank.service;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileStorageService {
	// yml 에 설정한 초기 파미미터 들어 오기 
    @Value("${file.upload-dir}")
    private String uploadDir;

    public void storeFile(MultipartFile file) {
        // 실행 경로 기준 상대 경로로 파일 저장 경로 생성
        Path targetLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        // 파일 저장 로직
    }
}