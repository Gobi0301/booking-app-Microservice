package com.booking.ms.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.booking.ms.model.BookingModel;

public interface BookingRepository extends MongoRepository<BookingModel, String>{

	Optional<BookingModel> findByBookingNumber(String bookingNumber);
	
	@Aggregation(pipeline = {
			"{$match :{customerNumber: ?0}}",
			"{$sort :{_id: -1}}"
	})
	List<BookingModel> findByCustomerNumber(String customerNumber);
}
