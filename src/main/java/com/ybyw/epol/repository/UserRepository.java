package com.ybyw.epol.repository;

import com.ybyw.epol.entity.User;
import com.ybyw.epol.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findFirstByEmail(String email);

    User findByRole(UserRole userRole);
}
