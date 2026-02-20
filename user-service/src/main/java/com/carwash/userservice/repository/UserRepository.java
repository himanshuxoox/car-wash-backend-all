package com.carwash.userservice.repository;

import com.carwash.userservice.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, String> {


    Optional<User> findByPhoneNumber(String phoneNumber);
}
