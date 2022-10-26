package com.visionrent.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.visionrent.dto.UserDTO;
import com.visionrent.dto.request.AdminUserUpdateRequest;
import com.visionrent.dto.request.UpdatePasswordRequest;
import com.visionrent.dto.request.UserUpdateRequest;
import com.visionrent.dto.response.ResponseMessage;
import com.visionrent.dto.response.VRResponse;
import com.visionrent.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {
	
	
	@Autowired
	private UserService userService;
	
	
	@GetMapping("/auth/all")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<List<UserDTO>> getAllUsers(){
		List<UserDTO> allUsers = userService.getAllUsers();
		return ResponseEntity.ok(allUsers);
	}
	
	// user will get their own information and admin will get their own information
	//http://localhost:8080/user
	@GetMapping()
	@PreAuthorize("hasRole('CUSTOMER') or hasRole('ADMIN')")
	public ResponseEntity<UserDTO> getUser(){
		UserDTO userDTO = userService.getPrincipal();
		
		return ResponseEntity.ok(userDTO);
	}
	//http://localhost:8080/user/auth/pages?page=0&size=1&sort=id&direction=ASC
	@GetMapping("/auth/pages")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<Page<UserDTO>> getAllUsersByPage(@RequestParam("page") int page,
																				@RequestParam("size") int size,
																				@RequestParam("sort") String prop,
																				@RequestParam(value="direction", required=false,defaultValue = "DESC") Direction direction){
		Pageable pageable = PageRequest.of(page, size, Sort.by(direction, prop));
	
		
		Page<UserDTO> userDTOPage = userService.getAll(pageable);
		
		return ResponseEntity.ok(userDTOPage);
	}
	
	// Admin create a request to get a user by id
	@GetMapping("/{id}/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<UserDTO> getUserById(@PathVariable Long id){
		UserDTO userDTO = userService.getUserById(id);
		
		return ResponseEntity.ok(userDTO);
	}
	
	@PatchMapping("/auth")
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<VRResponse> updatePassword(@Valid @RequestBody UpdatePasswordRequest updatePasswordRequest){
		userService.updatePassword(updatePasswordRequest);
		
		return new ResponseEntity<>(new VRResponse(ResponseMessage.PASSWORD_UPDATE_MESSAGE, true),HttpStatus.OK);
	}
	
	@PutMapping
	@PreAuthorize("hasRole('ADMIN') or hasRole('CUSTOMER')")
	public ResponseEntity<VRResponse> updateUser(@Valid @RequestBody UserUpdateRequest userUpdateRequest){
		
		userService.updateUser(userUpdateRequest);
		
		return new ResponseEntity<>(new VRResponse(ResponseMessage.USER_UPDATE_MESSAGE, true),HttpStatus.OK);
	}
	
	@PutMapping("/{id}/auth")
	@PreAuthorize("hasRole('ADMIN')")
	public ResponseEntity<VRResponse> updateUserAuth(@PathVariable Long id, @Valid @RequestBody AdminUserUpdateRequest adminUserUpdateRequest){
		userService.updateUserAuth(id, adminUserUpdateRequest);
		
		return new ResponseEntity<>(new VRResponse(ResponseMessage.USER_UPDATE_MESSAGE,true), HttpStatus.OK);
	}
	
	
	
	
	
	
	
	
	
	
	


}
