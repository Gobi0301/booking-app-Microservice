package com.review.ms.repository;


import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.review.ms.model.ReviewModel;

public interface ReviewRepository extends MongoRepository<ReviewModel, String> {

	  Optional<ReviewModel> findByCustomerNumber(String customerNumber);
}
