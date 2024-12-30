package com.wnc.banking.repository;

import com.wnc.banking.entity.ExternalTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExternalTransactionRepository extends JpaRepository<ExternalTransaction, Long> {
}
