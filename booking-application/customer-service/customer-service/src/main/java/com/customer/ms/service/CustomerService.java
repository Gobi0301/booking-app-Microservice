package com.customer.ms.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.customer.ms.dto.AuthRequestDto;
import com.customer.ms.dto.AuthResponseDto;
import com.customer.ms.dto.CustomerRequestDto;
import com.customer.ms.dto.CustomerResponseDto;
import com.customer.ms.message.ResponseMessage;
import com.customer.ms.model.CustomerModel;
import com.customer.ms.repository.CustomerRepository;
import com.customer.ms.util.JwtToken;

@Service
public class CustomerService {

	
	  @Autowired
	  private CustomerRepository customerRepository;

	  @Autowired
	  private PasswordEncoder passwordEncoder;

	  @Autowired
	  private JwtToken jwtService;

	  public ResponseMessage customerRegister(CustomerRequestDto customerRequest) {
	    CustomerModel customer = new CustomerModel();
	    customer.setFirstName(customerRequest.getFirstName());
	    customer.setLastName(customerRequest.getLastName());
	    customer.setEmail(customerRequest.getEmail());
	    customer.setCustNumber(randomCustomerNumber(8));
	    customer.setPassword(passwordEncoder.encode(customerRequest.getPassword()));

	    try {
	      customerRepository.insert(customer);
	    } catch (DataIntegrityViolationException e) {
	      throw new ResponseStatusException(HttpStatus.CONFLICT, "email already exists");
	    }

	    return new ResponseMessage("success");
	  }

	  public AuthResponseDto authenticateCustomer(AuthRequestDto authRequest) {
	    CustomerModel customer = customerRepository.findByEmail(authRequest.getEmail()).orElseThrow(
	        () -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid credentials"));

	    if (!passwordEncoder.matches(authRequest.getPassword(), customer.getPassword())) {
	      throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "invalid credentials");
	    }

	    AuthResponseDto authResponse = new AuthResponseDto();
	    authResponse.setEmail(customer.getEmail());
	    authResponse.setToken(jwtService.token(customer.getEmail()));

	    return authResponse;
	  }

	  public CustomerResponseDto getCustomerByNumber(String customerNumber) {
	    CustomerModel customer = customerRepository.findByCustNumber(customerNumber).orElseThrow(
	        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
	            String.format("cannot find customer by number %s", customerNumber)));

	    return new CustomerResponseDto(customer);
	  }

	  public CustomerResponseDto getCustomerByEmail(String email) {
	    CustomerModel customer = customerRepository.findByEmail(email).orElseThrow(
	        () -> new ResponseStatusException(HttpStatus.NOT_FOUND,
	            String.format("cannot find customer by email %s", email)));

	    return new CustomerResponseDto(customer);
	  }

	  private String randomCustomerNumber(int len) {
	    String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789";
	    Random rnd = new Random();
	    StringBuilder sb = new StringBuilder(len);

	    for (int i = 0; i < len; i++) {
	      sb.append(chars.charAt(rnd.nextInt(chars.length())));
	    }

	    return sb.toString();
	  }
}
