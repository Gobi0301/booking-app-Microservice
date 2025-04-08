package com.booking.ms.service;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.TimeZone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.booking.ms.dto.BookingEmailDto;
import com.booking.ms.dto.BookingRequestDto;
import com.booking.ms.dto.BookingResponseDto;
import com.booking.ms.dto.BookingTranscationRequestDto;
import com.booking.ms.dto.CustomerResponseDto;
import com.booking.ms.dto.TransactionDetailDto;
import com.booking.ms.dto.TranscationDto;
import com.booking.ms.dto.TranscationEmailDto;
import com.booking.ms.dto.TranscationResponseDto;
import com.booking.ms.message.ResponseMessage;
import com.booking.ms.model.BookingModel;
import com.booking.ms.repository.BookingRepository;
import com.booking.ms.type.BookingStatus;
import com.booking.ms.util.JwtToken;

@Service
public class BookingService {

	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private WebClient webClient;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private JwtToken jwtService;
	
	
	public ResponseMessage roomBooking(BookingRequestDto bookingRequest) {
		
		CustomerResponseDto customer = getCustomerDetail(bookingRequest.getCustomerNumber());
		TimeZone tz = TimeZone.getTimeZone("UTC");
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm'Z'");
		df.setTimeZone(tz);
		
		BookingModel booking = new BookingModel();
		booking.setRoomNumber(bookingRequest.getRoomNumber());
		booking.setCheckIn(bookingRequest.getCheckIn());
		booking.setCheckOut(bookingRequest.getCheckOut());
		booking.setCustomerNumber(bookingRequest.getCustomerNumber());
		booking.setRoomPrice(bookingRequest.getRoomPrice());
		booking.setBookingNumber(randomBookingNumber(10));
		booking.setBookedOn(df.format(new Date()));
		booking.setStatus(BookingStatus.BOOKED.status);
		
		try {
			bookingRepository.insert(booking);
		}catch(DataIntegrityViolationException e) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,"booking number already exists");
		}
	
		try {
			SimpleDateFormat newDate = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat formatter = new SimpleDateFormat("EEE , d MMM yyyy");
			BookingEmailDto bookingEmail = new BookingEmailDto();
			Date checkIn = newDate.parse(booking.getCheckIn());
			Date checkOut  = newDate.parse(booking.getCheckOut());
			
			
			bookingEmail.setFrom("server@localhost.com");
			bookingEmail.setTo(customer.getEmail());
			bookingEmail.setCustomerName(customer.getFirstName() + " " + customer.getLastName());
			bookingEmail.setRoomNo(booking.getRoomNumber().toString());
			bookingEmail.setExpectDate(formatter.format(checkIn));
			bookingEmail.setCheckIn(formatter.format(checkIn));
			bookingEmail.setCheckOut(formatter.format(checkOut));
			
			String token = jwtService.token("booking@techzoss.co.tz");
			System.out.println("Generated token: " + token);
			String uri = env.getProperty("application.service.notification.url", "http://127.0.0.1:8082");
			webClient.post()
			.uri(String.format("%s/api/mail/booking-message", uri))
			.headers(h -> h.setBearerAuth(token))
			.body(BodyInserters.fromValue(bookingEmail))
			.retrieve().bodyToMono(String.class)
			.block();
		}catch (WebClientResponseException | ParseException e) {
			// TODO: handle exception
			throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,e.getMessage());
		}
		
		return new ResponseMessage("booking success");
	}
	
	
	public BookingResponseDto getBookingByNumber(String bookingNumber) {
		BookingModel booking  = bookingRepository.findByBookingNumber(bookingNumber).orElseThrow(
				() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						String.format("cannot find booking by number %s", bookingNumber)));
		return new BookingResponseDto(booking);
	}
	
	public List<BookingResponseDto> getBookingByCustomerNumber(String customerNumber){
		List<BookingModel> bookings = bookingRepository.findByCustomerNumber(customerNumber);
		return bookings.stream().map(booking -> new BookingResponseDto(booking)).toList();
 	}
	
	public Boolean isCustomerBooked(String customerNumber) {
		List<BookingModel> bookings = bookingRepository.findByCustomerNumber(customerNumber);
		for(BookingModel booking : bookings) {
			if(booking.getStatus().equals(BookingStatus.BOOKED.name())) {
				return true;
			}
		}
		return false;
	}

	
	public BookingResponseDto bookingTransaction(BookingTranscationRequestDto bookingTranscation) {
		BookingModel booking  = bookingRepository.findByBookingNumber(
				bookingTranscation.getBookingNumber()).orElseThrow(
						() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
								String.format("cannot find booking by number %s", 
										bookingTranscation.getBookingNumber())));
		
			TranscationDto transcation = new TranscationDto();
			transcation.setBookingNumber(bookingTranscation.getBookingNumber());
			transcation.setCardNumber(bookingTranscation.getCardNumber());
			transcation.setCardExpiry(bookingTranscation.getCardExpiry());
			transcation.setCardCvc(bookingTranscation.getCardCvc());
			transcation.setAmount(Double.parseDouble(booking.getRoomPrice().toString()));
			transcation.setPaymentMode("CARD");
			
			try {
				String token = jwtService.token("booking@techzoss.co.tz");
				String uri = env.getProperty("application.service.payment.url", "http://127.0.0.1:8083");
			    TranscationResponseDto response = webClient.post()
				.uri(String.format("%s/api/transaction", uri))
				.headers(h -> h.setBearerAuth(token))
				.body(BodyInserters.fromValue(transcation))
				.retrieve().bodyToMono(TranscationResponseDto.class)
				.block();
			    
			    booking.setStatus(BookingStatus.BOOKED.status);
			    booking.setTranscationId(response.getTransctionId());
			    bookingRepository.save(booking);
		    
		}catch (WebClientResponseException e) {
			// TODO: handle exception
			if(e.getRawStatusCode() == 409) {
				throw new ResponseStatusException(e.getStatusCode(), "booking number transcation paid");
			}else if(e.getRawStatusCode() ==  406) {
				throw new ResponseStatusException(e.getStatusCode(), "invalid card details or insufficent balance");
			}
			
			throw new ResponseStatusException(e.getStatusCode());
		}
		
		CustomerResponseDto customer = getCustomerDetail(booking.getCustomerNumber());
		
		TransactionDetailDto transactionDetail = getTranscationDetail(booking.getTranscationId());
		
		try {
			SimpleDateFormat formatter = new SimpleDateFormat("EEE , d MMM yyyy hh:mm aaa");
			NumberFormat numberFormat = NumberFormat.getInstance(Locale.US);
			TranscationEmailDto transactioEmail = new TranscationEmailDto();
			String cardNumber = transactionDetail.getCardNumber();
			Double bookingAmount = transactionDetail.getAmount();
			Date paidDate  = Date.from(
					transactionDetail.getCreatedAt().atZone(ZoneId.systemDefault()).toInstant());
			StringBuffer amountSb = new StringBuffer();
			numberFormat.format(bookingAmount,amountSb,new FieldPosition(0));
			transactioEmail.setCustomerName(customer.getFirstName()+ " "+customer.getLastName());
			transactioEmail.setFrom("server@localhost.com");
			transactioEmail.setTo(customer.getEmail());
			transactioEmail.setPaidDate(formatter.format(paidDate));
			transactioEmail.setAmount(amountSb.toString());
			transactioEmail.setCardNo("******"+cardNumber.substring(cardNumber.length() - 4));
			transactioEmail.setCardType(transactionDetail.getCardType());
			
			String token = jwtService.token("booking@techzoss.co.tz");
			String uri = env.getProperty("application.service.notification.url", "http://127.0.0.1:8082");
			webClient.post()
			.uri(String.format("s/api/mail/transaction-message", uri))
			.headers(h -> h.setBearerAuth(token))
			.body(BodyInserters.fromValue(transactioEmail))
			.retrieve().bodyToMono(String.class)
			.block();
		}catch (WebClientResponseException e) {
			// TODO: handle exception
			throw new ResponseStatusException(e.getStatusCode());
		}
		return new BookingResponseDto(booking);
	}

	private TransactionDetailDto getTranscationDetail(Long id) {
		// TODO Auto-generated method stub
		try {
			String token = jwtService.token("booking@techzoss.co.tz");
			String uri = env.getProperty("application.service.payment.url","http://127.0.0.1:8083");
			TransactionDetailDto response = webClient.get()
					.uri(String.format("%s/api/transcation/%s", uri,id))
					.headers(h->h.setBearerAuth(token))
					.retrieve().bodyToMono(TransactionDetailDto.class)
					.block();
			return response;
		}catch (WebClientResponseException e) {
			// TODO: handle exception
			if(e.getRawStatusCode() == 404) {
				throw new ResponseStatusException(e.getStatusCode(), "booking transcation not found");
			}
			throw new ResponseStatusException(e.getStatusCode());
		}
	}


	private CustomerResponseDto getCustomerDetail(String customerNumber) {
		// TODO Auto-generated method stub
		try {
			String token = jwtService.token("booking@techzoss.co.tz");
			System.out.println("Generated Token: " + token);
			String uri = env.getProperty("application.service.customer.url", "http://127.0.0.1:8080");
			CustomerResponseDto customer = webClient.get()
					.uri(String.format("%s/api/customer?number=%s", uri,customerNumber))
					.headers(h -> h.setBearerAuth(token))
					.retrieve().bodyToMono(CustomerResponseDto.class)
					.block();
					
					return customer;
					
		}catch (WebClientResponseException e) {
			// TODO: handle exception
			if(e.getRawStatusCode() == 404) {
				throw new ResponseStatusException(e.getStatusCode(), "customer not found");
			}
			throw new ResponseStatusException(e.getStatusCode());
		}
		
	}
	
	private String randomBookingNumber(int len) {
		String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789abcdefghijklmnopqrstuvwxyz";
		Random rnd = new Random();
		StringBuilder sb = new StringBuilder(len);
		
		for(int i=0;i<len;i++) {
			sb.append(chars.charAt(rnd.nextInt(chars.length())));
		}
		return sb.toString();
	}
	
}
