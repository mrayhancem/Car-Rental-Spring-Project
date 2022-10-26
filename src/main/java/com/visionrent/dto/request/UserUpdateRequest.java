package com.visionrent.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateRequest {
	
	@NotBlank(message = "Please provide a first name")
	@Size(max = 50)
	private String firstName;
	
	@NotBlank(message = "Please provide a last name")
	@Size(max = 50)
	private String lastName;
	
	@Size(max = 50)
	@Email(message = "Please provide an email address")
	private String email;
	
	@Pattern(regexp = "^((\\(\\d{3}\\))|\\d{3})[- .]?\\d{3}[- .]?\\d{4}$",
			message = "Please provide valid phone number")
	@NotBlank(message = "Please provide a first name")
	@Size(max = 50)
	private String phoneNumber;
	
	@NotBlank(message = "Please provide an address")
	@Size(max = 50)
	private String address;
	
	@NotBlank(message = "Please provide a zip code")
	@Size(max = 50)
	private String zipCode;

}
