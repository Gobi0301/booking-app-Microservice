package com.booking.ms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TranscationEmailDto {

	private String to;
	
	private String from;
	
	private String customerName;
	
	private String paidDate;
	
	private String cardType;
	
	private String cardNo;
	
	private String amount;
}
