package com.customer.ms.dto;



import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequestDto {

	

	  @NotEmpty(message = "first name is required")
	  private String firstName;

	  @NotEmpty(message = "last name is required")
	  private String lastName;

	  @NotEmpty(message = "email is required")
	  @Email()
	  private String email;

	  @NotEmpty(message = "password is required")
	  private String password;
}
