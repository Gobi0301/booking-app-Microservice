package com.booking.ms.dto;

import com.booking.ms.model.BookingModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDto {

	private String bookingNumber;
	
	private String customerNumber;
	
	private Long roomNumber;
	
	private Long roomPrice;
	
	private String checkIn;
	
	private String checkOut;
	
	private String bookedOn;
	
	private String status;
	
	private Long transcationId;
	
	public BookingResponseDto(BookingModel booking) {
		bookingNumber = booking.getBookingNumber();
		customerNumber = booking.getCustomerNumber();
		roomNumber = booking.getRoomNumber();
		roomPrice = booking.getRoomPrice();
		checkIn = booking.getCheckIn();
		checkOut = booking.getCheckOut();
		bookedOn = booking.getBookedOn();
		status = booking.getStatus();
		transcationId = booking.getTranscationId();
	}
}
