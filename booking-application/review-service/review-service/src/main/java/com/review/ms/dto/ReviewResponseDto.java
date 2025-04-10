package com.review.ms.dto;

import java.util.Date;

import com.review.ms.model.ReviewModel;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponseDto {


	  private String customerNumber;

	  private String fullname;

	  private String email;

	  private String title;

	  private String description;

	  private Double rate;

	  private Date createdDate;

	  public ReviewResponseDto(ReviewModel review) {
	    customerNumber = review.getCustomerNumber();
	    fullname = review.getFullname();
	    email = review.getEmail();
	    title = review.getTitle();
	    description = review.getDescription();
	    rate = review.getRate();
	    createdDate = review.getCreatedDate();
	  }
}
