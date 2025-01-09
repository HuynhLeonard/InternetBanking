package com.wnc.banking.repository;

import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {
    List<Transaction> findBySenderAccountAndType(Account senderAccount, String type);
    List<Transaction> findByReceiverAccountAndType(Account receiverAccount, String type);
}
