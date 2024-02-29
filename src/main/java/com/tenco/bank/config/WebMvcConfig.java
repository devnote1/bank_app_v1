package com.tenco.bank.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.tenco.bank.handler.AuthInterceptor;

import lombok.RequiredArgsConstructor;

//@Configuration --> 스프링 부트 설정 클래스이다.
//내부에 메서드를 동작을 통한 Bean 객체 생성시 사용
@Configuration // IoC 대상  
@RequiredArgsConstructor // final 사용 시 사용(생성자 직접 정의 가능)   
public class WebMvcConfig implements WebMvcConfigurer {

	@Autowired // DI 
	private final AuthInterceptor authInterceptor;
	
	// 요청 올때 마다 domain URI 검사를 할 예정 
	// /account/xxx <- 으로 들어오는 도메인을 다 검사해!!! 
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(authInterceptor)
		        .addPathPatterns("/account/**")
		        .addPathPatterns("/auth/**");        
	}
	
}