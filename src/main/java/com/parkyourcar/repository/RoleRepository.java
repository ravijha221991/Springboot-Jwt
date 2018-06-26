package com.parkyourcar.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.parkyourcar.model.Role;
import com.parkyourcar.model.RoleName;

public interface RoleRepository extends JpaRepository<Role, Long>{
	Optional<Role> findByRoleName(RoleName name);

}
