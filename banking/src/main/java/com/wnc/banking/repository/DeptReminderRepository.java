package com.wnc.banking.repository;

import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.DeptReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptReminderRepository extends JpaRepository<DeptReminder, Integer> {
    List<DeptReminder> findBySenderAccountId(Account senderAccountId);
    List<DeptReminder>  findByReceiverAccountId(Account receiverAccountId);
}
