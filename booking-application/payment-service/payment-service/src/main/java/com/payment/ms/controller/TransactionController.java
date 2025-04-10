package com.payment.ms.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.payment.ms.dto.TransactionRequestDto;
import com.payment.ms.dto.TransactionResponseDto;
import com.payment.ms.model.TransactionModel;
import com.payment.ms.service.TransactionService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class TransactionController {


	  @Autowired
	  private TransactionService transactionService;

	  @PostMapping("/transaction")
	  @ResponseStatus(HttpStatus.CREATED)
	  public TransactionResponseDto cardTransaction(
	      @Valid @RequestBody TransactionRequestDto transactionRDto) {
	    return transactionService.cardTransaction(transactionRDto);
	  }

	  @GetMapping("/transaction/{id}")
	  @ResponseStatus(HttpStatus.OK)
	  public TransactionModel bookingTransaction(@PathVariable Long id) {
	    return transactionService.getBookingTransaction(id);
	  }
}
