package com.tenco.bank.repository.model;

import java.sql.Timestamp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User {
	private Integer id; 
	private String username;
	private String password; 
	private String originFileName; 
	private String uploadFileName;
	private String fullname; 
	private Timestamp createdAt; 
}
