package com.customer.ms.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.HttpStatus;
import com.customer.ms.dto.AuthRequestDto;
import com.customer.ms.dto.AuthResponseDto;
import com.customer.ms.dto.CustomerRequestDto;
import com.customer.ms.dto.CustomerResponseDto;
import com.customer.ms.message.ResponseMessage;
import com.customer.ms.service.CustomerService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api")
public class CustomerController {

	 @Autowired
	  private CustomerService customerService;

	  @PostMapping("/customer/register")
	  @ResponseStatus(HttpStatus.CREATED)
	  public ResponseMessage customerRegister(@Valid @RequestBody CustomerRequestDto customerRequest) {
	    return customerService.customerRegister(customerRequest);
	  }

	  @PostMapping("/customer/auth")
	  @ResponseStatus(HttpStatus.OK)
	  public AuthResponseDto authenticateCustomer(@Valid @RequestBody AuthRequestDto authRequest) {
	    return customerService.authenticateCustomer(authRequest);
	  }

	  @GetMapping("/customer")
	  @ResponseStatus(HttpStatus.OK)
	  public CustomerResponseDto getCustomerDetail(@RequestParam Optional<String> number,
	      @RequestParam Optional<String> email) {
	    if (!number.isEmpty()) {
	      return customerService.getCustomerByNumber(number.get());
	    } else if (!email.isEmpty()) {
	      return customerService.getCustomerByEmail(email.get());
	    }
	    return null;
	  }
}
