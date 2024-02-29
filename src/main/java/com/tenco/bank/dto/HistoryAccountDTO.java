package com.tenco.bank.dto;

import java.sql.Timestamp;

import com.tenco.bank.utils.ValueFormatter;

import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper=false)
//슈퍼클래스의 equals와 hashCode를 호출하지 않음을 명시
public class HistoryAccountDTO extends ValueFormatter {
	private Integer id;
	private Long amount; 
	private Long balance;
	private String sender; 
	private String receiver; 
	private Timestamp createdAt;
}	
