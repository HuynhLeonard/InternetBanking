package com.wnc.banking.repository;

import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.Receiver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReceiverRepository extends JpaRepository<Receiver, Integer> {
    List<Receiver> findBySenderAccountId(String senderAccountId);
    boolean existsBySenderAccountIdAndReceiverAccountId(String senderAccountId, String receiverAccountId);
    Receiver findBySenderAccountIdAndReceiverAccountId(String senderAccountId, String receiverAccountId);
}
