package com.parkyourcar.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.parkyourcar.exception.ResourceNotFoundException;
import com.parkyourcar.model.User;
import com.parkyourcar.repository.UserRepository;

@Service
public class CustomUserDetailService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String userNameOrEmail) throws UsernameNotFoundException {
		User user = userRepository.findByUserNameOrEmail(userNameOrEmail, userNameOrEmail)
										.orElseThrow(()->new UsernameNotFoundException("User not found with username or email:"+userNameOrEmail));
		return UserPrincipal.create(user);
	}

	public UserDetails loadUserById(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(()->new ResourceNotFoundException("User","id",userId));
		return UserPrincipal.create(user);
	}

}
