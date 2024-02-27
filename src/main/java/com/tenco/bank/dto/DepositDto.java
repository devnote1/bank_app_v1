package com.tenco.bank.dto;

import lombok.Data;

@Data
public class DepositDto {
	private Long amount;
	private String dAccountNumber;
}
