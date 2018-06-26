package com.parkyourcar.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.parkyourcar.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);
	Optional<User> findByUserNameOrEmail(String userName,String email);
	List<User> findByIdIn(List<Long> ids);
	Optional<User> findByUserName(String userName);
	Boolean existsByUserName(String userName);
	Boolean existsByEmail(String email);
}
