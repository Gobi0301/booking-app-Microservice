package com.booking.ms.error;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class NoBearerTokenError extends ResponseStatusException{

	public NoBearerTokenError() {
		super(HttpStatus.UNAUTHORIZED,"no bearer token");
		// TODO Auto-generated constructor stub
	}

	
}
