package com.visionrent.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Function;

import javax.validation.Valid;

import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.visionrent.domain.Role;
import com.visionrent.domain.User;
import com.visionrent.domain.enums.RoleType;
import com.visionrent.dto.UserDTO;
import com.visionrent.dto.request.AdminUserUpdateRequest;
import com.visionrent.dto.request.RegisterRequest;
import com.visionrent.dto.request.UpdatePasswordRequest;
import com.visionrent.dto.request.UserUpdateRequest;
import com.visionrent.exception.BadRequestException;
import com.visionrent.exception.ConflictException;
import com.visionrent.exception.ResourceNotFoundException;
import com.visionrent.exception.message.ErrorMessage;
import com.visionrent.mapper.UserMapper;
import com.visionrent.repository.UserRepository;
import com.visionrent.security.SecurityUtils;

@Service
public class UserService {
	
	
	private UserRepository userRepository;
	
	private PasswordEncoder passwordEncoder;
	
	private RoleService roleService;
	
	private UserMapper userMapper;
	
	public UserService(UserRepository userRepository, RoleService roleService,@Lazy PasswordEncoder passwordEncoder, UserMapper userMapper) {
		this.userRepository = userRepository;
		this.roleService = roleService;
		this.passwordEncoder = passwordEncoder;
		this.userMapper = userMapper;
	}
	
	public void saveUser(RegisterRequest registerRequest) {
		if(userRepository.existsByEmail(registerRequest.getEmail())) {
			throw new ConflictException(String.format(ErrorMessage.EMAIL_ALREADY_EXIST_MESSAGE, registerRequest.getEmail()));
		}
		
		Role role = roleService.findByType(RoleType.ROLE_CUSTOMER);
		Set<Role> roles = new HashSet<>();
		roles.add(role);
		
		User user = new User();
		user.setFirstName(registerRequest.getFirstName());
		user.setLastName(registerRequest.getLastName());
		user.setAddress(registerRequest.getAddress());
		user.setPhoneNumber(registerRequest.getPhoneNumber());
		user.setEmail(registerRequest.getEmail());
		user.setZipCode(registerRequest.getZipCode());
		user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));
		user.setRoles(roles);
		
		userRepository.save(user);
	}
	
	public User getUserByEmail(String email) {
		User user = userRepository.findByEmail(email).orElseThrow(() -> 
		new ResourceNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_MESSAGE, email)));
		
		return user;
	}
	
	public List<UserDTO> getAllUsers(){
		List<User> users = userRepository.findAll();
		List<UserDTO> userDTOS = userMapper.userToUserDTO(users);
		
		return userDTOS;
	}

	public UserDTO getPrincipal() {
		User user = getCurrentUser();
		UserDTO userDTO = userMapper.userToUserDTO(user);
		
		return userDTO;
		
	}
	
	public User getCurrentUser() {
		String email =  SecurityUtils.getCurrentUserLogin()
				.orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessage.PRINCIPAL_NOT_FOUND_MESSAGE)));
		User user =  getUserByEmail(email);
		
		return user;
	}
	
	//getuserBy ID
	
	public UserDTO getUserById(Long id) {
		User user = userRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException(String.format(ErrorMessage.USER_NOT_FOUND_MESSAGE, id)));
		UserDTO userDTO = userMapper.userToUserDTO(user);
		
		return userDTO;
	}
	
	
	
	public Page<UserDTO> getAll(Pageable pageable){
		
		Page<User> userPage = userRepository.findAll(pageable);
		 
		return getUserDTOPage(userPage);
	}

	private Page<UserDTO> getUserDTOPage(Page<User> userPage) {
		Page<UserDTO> userDTOPage = userPage.map(new Function<User, UserDTO>(){

			@Override
			public UserDTO apply(User t) {
				// TODO Auto-generated method stub
				return userMapper.userToUserDTO(t);
			}
			
		});
		
		return userDTOPage;
	}

	public void updatePassword(UpdatePasswordRequest updatePasswordRequest) {
		
		User user = getCurrentUser();
		
		//built in kullanici ise, degisimi engelle
		if(user.getBuiltIn()) {
			throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
		}
		
		if(!passwordEncoder.matches(updatePasswordRequest.getOldPassword(), user.getPassword())) {
			throw new BadRequestException(ErrorMessage.PASSWORD_NOT_MATCH_MESSAGE);
		}
		
		String hashedPass = passwordEncoder.encode(updatePasswordRequest.getNewPassword());
		user.setPassword(hashedPass);
		userRepository.save(user);
		
		
	}
	
	@Transactional
	public void updateUser(@Valid UserUpdateRequest userUpdateRequest) {
		
		
		User user = getCurrentUser();
		if(user.getBuiltIn()) {
			throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
		}
		boolean emailExist = userRepository.existsByEmail(userUpdateRequest.getEmail());
		if(emailExist && !userUpdateRequest.getEmail().equals(user.getEmail())) {
			throw new ConflictException(String.format(ErrorMessage.EMAIL_ALREADY_EXIST_MESSAGE,userUpdateRequest.getEmail()));
		}
		
		userRepository.update(user.getId(), userUpdateRequest.getFirstName(), userUpdateRequest.getLastName(), userUpdateRequest.getPhoneNumber(), userUpdateRequest.getEmail(), userUpdateRequest.getAddress(), userUpdateRequest.getZipCode());
		
	}

	public void updateUserAuth(Long id, @Valid AdminUserUpdateRequest adminUserUpdateRequest) {
		
		
		User user = userRepository.findUserById(id).orElseThrow(()-> new ResourceNotFoundException(String.format(ErrorMessage.RESOURCE_NOT_FOUND_MESSAGE, id)));
		
		if(user.getBuiltIn()) {
			throw new BadRequestException(ErrorMessage.NOT_PERMITTED_METHOD_MESSAGE);
		}
		boolean emailExist = userRepository.existsByEmail(adminUserUpdateRequest.getEmail());
		
		if(emailExist && !adminUserUpdateRequest.getEmail().equals(user.getEmail())) {
			throw new ConflictException(String.format(ErrorMessage.EMAIL_ALREADY_EXIST_MESSAGE,adminUserUpdateRequest.getEmail()));
		}
		
		if(adminUserUpdateRequest.getPassword() == null) {
			adminUserUpdateRequest.setPassword(user.getPassword());
		}
		else {
			adminUserUpdateRequest.setPassword(passwordEncoder.encode(adminUserUpdateRequest.getPassword()));
		}
		
		Set<String> userStrRoles = adminUserUpdateRequest.getRoles();
		Set<Role> roles = convertRoles(userStrRoles);
		
		user.setFirstName(adminUserUpdateRequest.getFirstName());
		user.setLastName(adminUserUpdateRequest.getLastName());
		user.setEmail(adminUserUpdateRequest.getEmail());
		user.setAddress(adminUserUpdateRequest.getAddress());
		user.setPassword(adminUserUpdateRequest.getPassword());
		user.setZipCode(adminUserUpdateRequest.getZipCode());
		user.setBuiltIn(adminUserUpdateRequest.getBuiltIn());
		
		user.setRoles(roles);
		
		userRepository.save(user);
	}
	
	public Set<Role> convertRoles(Set<String> pRoles){
		Set<Role> roles = new HashSet<>();
		Role role = new Role();
		
		if(pRoles == null) {
			Role userRole = roleService.findByType(RoleType.ROLE_CUSTOMER);
			roles.add(userRole);
		}else {
//			pRoles.forEach(roller -> 
//			roller.equals(RoleType.ROLE_ADMIN) ? 
//					roles.add(roleService.findByType(RoleType.ROLE_ADMIN)) : 
//						roles.add(roleService.findByType(RoleType.ROLE_CUSTOMER)))));
			pRoles.forEach(roleStr -> {
				if(roleStr.equals(RoleType.ROLE_CUSTOMER.getName())) {
					//role.setType(RoleType.ROLE_CUSTOMER);
					roles.add(roleService.findByType(RoleType.ROLE_CUSTOMER));
					//roles.add(role);
				}else {
					roles.add(roleService.findByType(RoleType.ROLE_ADMIN));
					//role.setType(RoleType.ROLE_ADMIN);
					//roles.add(role);
				}
			});
		}
		return roles;
	}
	
	public void removeUserById(Long id) {
		
	}

	
	
}
