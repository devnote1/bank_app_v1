package com.tenco.bank.dto;

import java.sql.Timestamp;

import lombok.Data;

// model 패키지가 아님 DTO 패키지에 넣는 이유는 왜 일까? 
@Data
public class HistoryAccountDTO {
	private Integer id;
	private Long amount; 
	private Long balance;
	private String sender; 
	private String receiver; 
	private Timestamp createdAt;
}	
