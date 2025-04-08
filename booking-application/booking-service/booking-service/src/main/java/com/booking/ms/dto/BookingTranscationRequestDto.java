package com.booking.ms.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingTranscationRequestDto {

	@NotEmpty(message = "booking number is required")
	private String bookingNumber;
	
	@NotEmpty(message = "card number is required")
	private String cardNumber;
	
	@NotEmpty(message = "card expiration date is required")
	private String cardExpiry;
	
	@NotEmpty(message = "card CVC is required")
	private String cardCvc;
}
