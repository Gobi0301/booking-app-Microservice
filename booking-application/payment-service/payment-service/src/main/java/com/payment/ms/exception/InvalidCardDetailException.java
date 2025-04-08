package com.payment.ms.exception;

public class InvalidCardDetailException extends RuntimeException {
	  public InvalidCardDetailException() {
		    super("invalid card details");
		  }

}
