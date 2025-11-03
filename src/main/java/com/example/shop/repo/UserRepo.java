package com.example.shop.repo;


import com.example.shop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;


public interface UserRepo extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);
}