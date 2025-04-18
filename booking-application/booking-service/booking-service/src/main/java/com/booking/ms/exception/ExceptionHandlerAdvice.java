package com.booking.ms.exception;

import java.net.ConnectException;
import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.booking.ms.error.ApiError;

import jakarta.servlet.http.HttpServletRequest;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

	@ExceptionHandler({MethodArgumentNotValidException.class})
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	ApiError handleValidationException(MethodArgumentNotValidException exception, 
			 HttpServletRequest request) {
		
		ApiError apiError = new ApiError(HttpStatus.BAD_REQUEST.value(),"validation error");
		BindingResult result = exception.getBindingResult();
		Map<String,String> validationErrors = new HashMap<>();
		
		for(FieldError fieldError : result.getFieldErrors()) {
			validationErrors.put(fieldError.getField(), fieldError.getDefaultMessage());
		}
		
		apiError.setValidationErrors(validationErrors);
		
		return apiError;
		
	}
	
	@ExceptionHandler({ConnectException.class})
	@ResponseStatus(HttpStatus.SERVICE_UNAVAILABLE)
	ApiError handleConnectException(ConnectException exception, HttpServletRequest request) {
		ApiError apiError = new ApiError(HttpStatus.SERVICE_UNAVAILABLE.value(),"service unavailable");
		return apiError;
	}
	
	
	
}
