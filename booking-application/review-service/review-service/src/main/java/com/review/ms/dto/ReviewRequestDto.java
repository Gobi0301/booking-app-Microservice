package com.review.ms.dto;

import org.springframework.format.annotation.NumberFormat;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewRequestDto {

	  @NotEmpty(message = "review title is required")
	  private String title;

	  @NotEmpty(message = "review description is required")
	  private String description;

	  @NotNull(message = "review rate is required")
	  @NumberFormat
	  private Double rate;

	  @NotEmpty(message = "customer number is required")
	  private String customerNumber;
}
