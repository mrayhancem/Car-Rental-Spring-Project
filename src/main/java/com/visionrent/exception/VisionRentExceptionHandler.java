package com.visionrent.exception;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.visionrent.exception.message.ApiResponseError;

@ControllerAdvice
public class VisionRentExceptionHandler extends ResponseEntityExceptionHandler{
	
	Logger logger = LoggerFactory.getLogger(VisionRentExceptionHandler.class);
	
	private ResponseEntity<Object> buildResponseEntity(ApiResponseError error){
		logger.error(error.getMessage());
		return new ResponseEntity<Object>(error , error.getStatus());
	}
	
	
	@ExceptionHandler(ResourceNotFoundException.class)
	protected ResponseEntity<Object> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request){
		
//		Map<String, String> map = new LinkedHashMap<>();
//		map.put("time",LocalDateTime.now().toString());
//		map.put("message", ex.getMessage());
//		map.put("path", request.getDescription(false));
//		
//		return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);
		
		ApiResponseError error = new ApiResponseError(HttpStatus.NOT_FOUND, ex.getMessage(), request.getDescription(true));
		return buildResponseEntity(error);
	}
	
	@ExceptionHandler(ConflictException.class)
	protected ResponseEntity<Object> handleConflictException(ConflictException ex, WebRequest request){
		
	
		ApiResponseError error = new ApiResponseError(HttpStatus.CONFLICT, ex.getMessage(), request.getDescription(true));
		return buildResponseEntity(error);
	}
	
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		List<String> errors = ex.getBindingResult().getFieldErrors().stream().map((e)-> e.getDefaultMessage()).collect(Collectors.toList());
		ApiResponseError error = new ApiResponseError(status, errors.get(0).toString(),request.getDescription(false));
		
		return buildResponseEntity(error);
	}
	
	@ExceptionHandler(BadRequestException.class)
	protected ResponseEntity<Object> handleBadRequestException(BadRequestException ex, WebRequest request){
		
	
		ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST, ex.getMessage(), request.getDescription(true));
		return buildResponseEntity(error);
	}
	
	
	
	@Override
	protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers,
			HttpStatus status, WebRequest request) {
		
		ApiResponseError error = new ApiResponseError(HttpStatus.BAD_REQUEST, ex.getMessage(),request.getDescription(false));
		
		return buildResponseEntity(error);
	}
	
	@ExceptionHandler(AccessDeniedException.class)
	protected ResponseEntity<Object> handleAccessDeniedException(AccessDeniedException ex, WebRequest request){
		
		//if user enters incorrect information/access privilege
	
		ApiResponseError error = new ApiResponseError(HttpStatus.FORBIDDEN, ex.getMessage(), request.getDescription(true));
		return buildResponseEntity(error);
	}
	
	
	
	@Override
	protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		ApiResponseError error = new ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),request.getDescription(false));
		
		return buildResponseEntity(error);
	}
	
	
	@ExceptionHandler(RuntimeException.class)
	protected ResponseEntity<Object> handleRuntimeException(RuntimeException ex, WebRequest request){
		
		ApiResponseError error = new ApiResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage(),request.getDescription(false));
		
		return buildResponseEntity(error);
	}
}
