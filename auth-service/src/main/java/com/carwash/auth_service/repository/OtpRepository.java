package com.carwash.auth_service.repository;

import com.carwash.auth_service.domain.OtpRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<OtpRecord, Long> {

    Optional<OtpRecord> findTopByPhoneNumberAndVerifiedFalseOrderByCreatedAtDesc(String phoneNumber);

    Optional<OtpRecord> findByPhoneNumberAndOtpAndVerifiedFalse(String phoneNumber, String otp);

    void deleteByPhoneNumberAndExpiresAtBefore(String phoneNumber, LocalDateTime dateTime);
}