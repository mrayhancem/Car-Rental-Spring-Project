package com.visionrent.dto.request;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ContactMessageRequest {
	
	@Size(min=1,max=50,message="Your Name '${validatedValue}' must be between {min} and {max} chars long")
	@NotBlank(message = "Please provide a name")
	private String name;
	
	@Size(min=1,max=50,message="Subject '${validatedValue}' must be between {min} and {max} chars long")
	@NotBlank(message = "Please provide a subject")
	private String subject;
	
	@Size(min=1,max=50,message="Your Name '${validatedValue}' must be between {min} and {max} chars long")
	@NotBlank(message = "Please provide a message")
	private String body;
	
	
	@NotBlank(message = "Please provide a valid email address")
	@Email
	private String email;
}
