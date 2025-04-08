package com.payment.ms.exception;

public class InsufficientBalanceException extends RuntimeException {
	  public InsufficientBalanceException() {
		    super("insufficient balance in account");
		  }
		}
