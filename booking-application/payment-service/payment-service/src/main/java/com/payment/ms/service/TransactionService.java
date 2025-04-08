package com.payment.ms.service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import com.payment.ms.dto.TransactionRequestDto;
import com.payment.ms.dto.TransactionResponseDto;
import com.payment.ms.model.TransactionModel;
import com.payment.ms.repository.TransactionRepository;
import com.payment.ms.util.JwtToken;
import com.payment.ms.util.PaymentGatewaySimulator;
import com.payment.ms.util.PaymentGatewaySimulator.CardRequest;
import com.payment.ms.util.PaymentGatewaySimulator.PaymentResponse;



@Service
public class TransactionService {

	
	@Autowired
	  private TransactionRepository transactionRepository;

	  @Autowired
	  private WebClient webClient;

	  @Autowired
	  private Environment env;

	  @Autowired
	  private JwtToken jwtService;

	  @Transactional
	  public TransactionResponseDto cardTransaction(TransactionRequestDto transactionRDto) {
	    try {
	      // check if booking number is valid from booking service
	      Boolean isValidBookingNumber = isBookingNumberValid(transactionRDto.getBookingNumber());
	      if (!isValidBookingNumber) {
	        throw new ResponseStatusException(HttpStatus.NOT_ACCEPTABLE, "invalid booking number");
	      }

	      // save transaction details
	      TransactionModel transaction = new TransactionModel();
	      transaction.setPaymentMode(transactionRDto.getPaymentMode());
	      transaction.setCardNumber(transactionRDto.getCardNumber());
	      transaction.setBookingNumber(transactionRDto.getBookingNumber());
	      transaction.setAmount(transactionRDto.getAmount());

	      TransactionModel savedTransaction = transactionRepository.save(transaction);

	      // simulate transaction failure due to insufficient balance in account
	      CardRequest cardRequest = new CardRequest(
	          transactionRDto.getCardNumber(), transactionRDto.getCardExpiry(),
	          transactionRDto.getCardCvc(), transactionRDto.getAmount());
	      PaymentResponse payment = PaymentGatewaySimulator.validateAccBalance(cardRequest);

	      transaction.setCardType(payment.cardType());
	      transaction.setBankName(payment.bankName());

	      savedTransaction = transactionRepository.save(transaction);

	      TransactionResponseDto response = new TransactionResponseDto();
	      response.setTransactionId(savedTransaction.getId());

	      return response;
	    } catch (DataIntegrityViolationException ex) {
	      throw new ResponseStatusException(HttpStatus.CONFLICT, "booking number transaction paid");
	    }
	  }

	  public TransactionModel getBookingTransaction(Long id) {
	    return transactionRepository.findById(id).orElseThrow(
	        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
	            String.format("cannot find booking transaction by ID %s", id)));
	  }

	  private Boolean isBookingNumberValid(String bookingNumber) {
	    // check if booking number is valid from booking service
	    try {
	      String token = jwtService.token("payment@project19.co.tz");
	      String uri = env.getProperty("application.service.booking.url", "http://127.0.0.1:8081");
	      webClient.get()
	          .uri(String.format("%s/api/booking/%s", uri, bookingNumber))
	          .headers(h -> h.setBearerAuth(token))
	          .retrieve().bodyToMono(String.class)
	          .block();

	      return true;
	    } catch (WebClientResponseException we) {
	      return false;
	    }
	  }
}
