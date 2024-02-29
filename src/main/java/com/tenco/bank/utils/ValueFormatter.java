package com.tenco.bank.utils;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;

// 추상 클래스에 또 다른 용도 
// ValueFormatter 클래스를 직접 인스턴스화(객체) 하지 않도록 강제성 추가
public abstract class ValueFormatter {
	
	public String timestampToString(Timestamp timestamp) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		return sdf.format(timestamp); 
	}
	
	public String formatKoreanWon(Long amount) {
		DecimalFormat df = new DecimalFormat("#,###");
		String formatNumber = df.format(amount);
		return formatNumber + "원";
	}
}
