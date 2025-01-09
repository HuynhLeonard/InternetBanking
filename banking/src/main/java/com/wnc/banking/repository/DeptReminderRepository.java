package com.wnc.banking.repository;

import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.DeptReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeptReminderRepository extends JpaRepository<DeptReminder, Integer> {
    List<DeptReminder> findBySenderAccountId(String senderAccountId);
    List<DeptReminder>  findByReceiverAccountId(String receiverAccountId);
    DeptReminder findBySenderAccountIdAndReceiverAccountIdAndStatusAndAmount(String senderAccountId, String receiverAccountId, String status, Long amount);
    List<DeptReminder> findDeptReminderBySenderAccountIdOrReceiverAccountId(String senderAccountId, String receiverAccountId);
}
