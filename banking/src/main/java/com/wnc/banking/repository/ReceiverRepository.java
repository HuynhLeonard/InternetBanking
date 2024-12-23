package com.wnc.banking.repository;

import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, Integer> {
    Receiver findBySenderAccountIdAndAndReceiverAccountId(Account senderAccountId, Account receiverAccountId);
}
