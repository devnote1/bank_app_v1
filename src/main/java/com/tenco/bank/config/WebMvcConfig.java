package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.bank.handler.AuthInterceptor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired
	private final AuthInterceptor authInterceptor;
	// 코드 수정 
	@Value("${file.upload-dir}")
	private String uploadDir;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor).addPathPatterns("/account/**").addPathPatterns("/auth/**");
	}

	// 코드 수정 
	// 'file:' 접두사를 사용하여 파일 시스템의 절대 경로를 나타냅니다.
    // 예: "file:///uploads/" (리눅스 또는 MacOS), "file:///C:/uploads/" (Windows)
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/images/uploads/**")
				.addResourceLocations("file:" + uploadDir);
	}

	@Bean // IoC 대상 - 싱글톤 처리
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

}