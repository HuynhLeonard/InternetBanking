package com.wnc.banking.service;

import com.wnc.banking.dto.DeptReminderDTO;
import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.DeptReminder;
import com.wnc.banking.repository.AccountRepository;
import com.wnc.banking.repository.DeptReminderRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

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

    public List<String> createDeptReminder(DeptReminderDTO deptReminderDTO) {
        DeptReminder deptReminder = new DeptReminder();

        Account senderAccount = accountRepository.findByAccountNumber(deptReminderDTO.getSenderAccountNumber());
        Account receiverAccount = accountRepository.findByAccountNumber(deptReminderDTO.getReceiverAccountNumber());

        if (senderAccount == null) {
            List<String> result = new ArrayList<>();
            result.add("Cannot find sender account");
            if (receiverAccount == null) {
                result.add("Cannot find receiver account");
            }
            return result;
        }

        deptReminder.setSenderAccountId(senderAccount);
        deptReminder.setReceiverAccountId(receiverAccount);
        deptReminder.setAmount(deptReminderDTO.getAmount());
        deptReminder.setDescription(deptReminderDTO.getDescription());
        deptReminder.setStatus("Chưa thanh toán");
        deptReminder.setCreatedAt(LocalDateTime.now());

        deptReminderRepository.save(deptReminder);
        return List.of("Create dept reminder successfully");
    }

    public String deleteDeptReminder(Integer deptReminderId) {
        Optional<DeptReminder> deptReminder = deptReminderRepository.findById(deptReminderId);
        if (deptReminder.isPresent()) {
            deptReminderRepository.delete(deptReminder.get());
            return "Delete dept reminder successfully";
        } else {
            return "Cannot find dept reminder";
        }
    }

    public String payDeptReminder(DeptReminderDTO deptReminderDTO) {return "";}
}
