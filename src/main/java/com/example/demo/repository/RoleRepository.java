package com.example.demo.repository;

import com.example.demo.model.Role;
import com.example.demo.model.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(RoleName name);
}
