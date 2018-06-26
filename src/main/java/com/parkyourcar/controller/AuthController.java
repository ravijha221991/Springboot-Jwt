package com.parkyourcar.controller;

import java.net.URI;
import java.util.Collections;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.parkyourcar.exception.AppException;
import com.parkyourcar.model.Role;
import com.parkyourcar.model.RoleName;
import com.parkyourcar.model.User;
import com.parkyourcar.payload.ApiResponse;
import com.parkyourcar.payload.JwtAuthenticationResponse;
import com.parkyourcar.payload.LoginRequest;
import com.parkyourcar.payload.SignupRequest;
import com.parkyourcar.repository.RoleRepository;
import com.parkyourcar.repository.UserRepository;
import com.parkyourcar.security.JwtTokenProvider;


@RestController
@RequestMapping("/api/auth")
public class AuthController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private RoleRepository roleRepository;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtTokenProvider tokenProvider;
	
	@PostMapping("/signup")
	public ResponseEntity<?> registerUser(@Valid @RequestBody SignupRequest request){
		if(userRepository.existsByUserName(request.getUsername())){
			return new ResponseEntity(new ApiResponse(false, "Username is already taken."),HttpStatus.BAD_REQUEST);
		}
		if(userRepository.existsByEmail(request.getUsername())){
			return new ResponseEntity(new ApiResponse(false, "Email is already in use."),HttpStatus.BAD_REQUEST);
		}
		User user = new User();
		user.setName(request.getName());
		user.setUserName(request.getUsername());
		user.setEmail(request.getEmail());
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		Role role = roleRepository.findByRoleName(RoleName.ROLE_USER)
									.orElseThrow(()-> new AppException("User role not set"));
		user.setRoles(Collections.singleton(role));
		User savedUser = userRepository.save(user);
		URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/users/{username}")
                .buildAndExpand(savedUser.getUserName()).toUri();
		return ResponseEntity.created(location).body(new ApiResponse(true, "User registered successfully"));
	}
	
	
	@PostMapping("/signin")
	public ResponseEntity<?> signin(@Valid @RequestBody LoginRequest request){
		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsernameOrEmail(), request.getPassword()));
		SecurityContextHolder.getContext().setAuthentication(authentication);
		String jwt = tokenProvider.generateToken(authentication);
		return ResponseEntity.ok(new JwtAuthenticationResponse(jwt));
	}

}
