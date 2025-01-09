package com.wnc.banking.repository;

import com.wnc.banking.entity.ExternalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ExternalTransactionRepository extends JpaRepository<ExternalTransaction, Long> {
    List<ExternalTransaction> findExternalTransactionByTypeAndAccountNumber(String type, String accountNumber);
}
