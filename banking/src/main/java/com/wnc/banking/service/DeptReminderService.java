package com.wnc.banking.service;

import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.DeptReminder;
import com.wnc.banking.repository.AccountRepository;
import com.wnc.banking.repository.DeptReminderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class DeptReminderService {
    private final DeptReminderRepository deptReminderRepository;
    private final AccountRepository accountRepository;

    public List<DeptReminder> getBySenderAccountNumber(String senderAccountNumber) {
        Account senderAccount = accountRepository.findByAccountNumber(senderAccountNumber);
        if (senderAccount == null) {
            return null;
        }
        return deptReminderRepository.findBySenderAccountId(senderAccount);
    }

    public List<DeptReminder> getByReceiverAccountNumber(String receiverAccountNumber) {
        Account receiverAccount = accountRepository.findByAccountNumber(receiverAccountNumber);
        if (receiverAccount == null) {
            return null;
        }
        return deptReminderRepository.findByReceiverAccountId(receiverAccount);
    }
}
