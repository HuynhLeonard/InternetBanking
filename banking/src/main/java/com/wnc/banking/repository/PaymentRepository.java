package com.wnc.banking.repository;

import com.wnc.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Account, Integer> {
    Account getPaymentByCustomerId(String customerId);
    List<Account> getPaymentsByCreatedAt(LocalDateTime createdAt);
}
