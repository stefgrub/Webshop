package com.example.shop.repo;

import com.example.shop.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UserRepo extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByEmailIgnoreCase(String email);

    // Robust: findet ADMIN und ROLE_ADMIN (Groß/Kleinschreibung egal) UND nur verifizierte E-Mails
    @Query("""
           select u from User u
           where u.emailVerified = true
             and u.email is not null and u.email <> ''
             and lower(u.role) in ('admin','role_admin')
           """)
    List<User> findAdminsVerified();

}