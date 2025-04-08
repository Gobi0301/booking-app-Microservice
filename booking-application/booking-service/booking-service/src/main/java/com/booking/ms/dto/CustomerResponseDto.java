package com.booking.ms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerResponseDto {

	private String custNumber;
	private String firstName;
	private String lastName;
	private String email;
}
