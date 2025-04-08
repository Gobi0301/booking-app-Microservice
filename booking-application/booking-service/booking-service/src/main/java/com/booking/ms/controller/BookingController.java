package com.booking.ms.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.booking.ms.dto.BookingRequestDto;
import com.booking.ms.dto.BookingResponseDto;
import com.booking.ms.dto.BookingTranscationRequestDto;
import com.booking.ms.message.ResponseMessage;
import com.booking.ms.service.BookingService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class BookingController {

	@Autowired
	private BookingService bookingService;
	
	@PostMapping("/booking")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseMessage roomBooking(@Valid @RequestBody BookingRequestDto bookingRequest) {
		return bookingService.roomBooking(bookingRequest);
	}
	
	@GetMapping("/customer-booked/{number}")
	@ResponseStatus(HttpStatus.OK)
	public boolean isCustomerBooked(@PathVariable String number) {
		return bookingService.isCustomerBooked(number);
	}
	
	
	
	@GetMapping("/booking")
	@ResponseStatus(HttpStatus.OK)
	public List<BookingResponseDto> customerBookingDetail(@RequestParam Optional<String> number){
		if(!number.isEmpty()) {
			return bookingService.getBookingByCustomerNumber(number.get());
		}
		
		return null;
	}
	
	@GetMapping("/booking/{bookingNumber}")
	@ResponseStatus(HttpStatus.OK)
	public BookingResponseDto getBooking(@PathVariable String bookingNumber) {
		return bookingService.getBookingByNumber(bookingNumber);
	}
	
	
	@PostMapping("/booking/transaction")
	@ResponseStatus(HttpStatus.CREATED)
	public BookingResponseDto bookingTransaction(@Valid @RequestBody BookingTranscationRequestDto bookingTranscation) {
		return bookingService.bookingTransaction(bookingTranscation);
	}
}
