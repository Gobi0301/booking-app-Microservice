package com.booking.ms.dto;

import org.springframework.format.annotation.NumberFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDto {

	
	@NotEmpty(message = "check date is required")
	private String checkIn;
	
	@NotEmpty(message = "check out date is required")
	private String checkOut;
	
	@NotEmpty(message = "customer number is required")
	private String customerNumber;
	
	@NotNull(message = "room price is required")
	@NumberFormat
	private Long roomPrice;
	
	@NotNull(message = "room number is required")
	@NumberFormat
	private Long roomNumber;
	
}
