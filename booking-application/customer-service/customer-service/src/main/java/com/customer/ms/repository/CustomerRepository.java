package com.customer.ms.repository;

import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.customer.ms.model.CustomerModel;

public interface CustomerRepository extends MongoRepository<CustomerModel, String>{

	  Optional<CustomerModel> findByCustNumber(String customerNumber);

	  Optional<CustomerModel> findByEmail(String email);
}
