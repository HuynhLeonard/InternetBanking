package com.wnc.banking.service;

import com.wnc.banking.dto.ReceiverDTO;
import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.Customer;
import com.wnc.banking.entity.Receiver;
import com.wnc.banking.repository.AccountRepository;
import com.wnc.banking.repository.CustomerRepository;
import com.wnc.banking.repository.ReceiverRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@AllArgsConstructor
public class ReceiverService {
    private final ReceiverRepository receiverRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;



    public List<Receiver> getReceiversByAccountNumber(String senderAccountNumber) {
        Account senderAccount = accountRepository.findByAccountNumber(senderAccountNumber);
        if (senderAccount == null) {
            return null;
        }

        return receiverRepository.findBySenderAccountId(senderAccount.getId());
    }

    public List<String> createReceiver(ReceiverDTO receiverDTO) {
        Receiver receiver = new Receiver();

        Account senderAccount = accountRepository.findByAccountNumber(receiverDTO.getSenderAccountNumber());
        Account receiverAccount = accountRepository.findByAccountNumber(receiverDTO.getReceiverAccountNumber());

        if (senderAccount == null) {
            List<String> result = new ArrayList<>();
            result.add("Cannot find sender account");
            if (receiverAccount == null) {
                result.add("Cannot find receiver account");
            }
            return result;
        }

        if (receiverRepository.existsBySenderAccountIdAndReceiverAccountId(senderAccount.getId(), receiverAccount.getId())) {
            String message = updateReceiver(receiverDTO);
            return List.of(message);
        } else {
            receiver.setSenderAccountId(receiverDTO.getSenderAccountNumber());
            receiver.setReceiverAccountId(receiverDTO.getReceiverAccountNumber());
            receiver.setType(receiverDTO.getType());
            receiver.setBankId(receiverDTO.getBankId());
            if (receiverDTO.getNickName() != null && !receiverDTO.getNickName().isEmpty()) {
                receiver.setNickName(receiverDTO.getNickName());
            } else {
                Customer customer = customerRepository.findByAccount(receiverAccount);
                String nickname = customer.getName();
                receiver.setNickName(nickname);
            }

            receiver.setCreatedAt(LocalDateTime.now());
            receiver.setUpdatedAt(LocalDateTime.now());

            receiverRepository.save(receiver);
            return List.of("Create receiver successfully");
        }
    }

    public String updateReceiver(ReceiverDTO receiverDTO) {
        Account senderAccount = accountRepository.findByAccountNumber(receiverDTO.getSenderAccountNumber());
        Account receiverAccount = accountRepository.findByAccountNumber(receiverDTO.getReceiverAccountNumber());
        Receiver receiver = receiverRepository.findBySenderAccountIdAndReceiverAccountId(senderAccount.getId(), receiverAccount.getId());

        if (receiver == null) {
            return "Cannot find sender account and receiver account";
        }

        if (receiverDTO.getNickName() != null && !receiverDTO.getNickName().isEmpty()) {
            receiver.setNickName(receiverDTO.getNickName());
        } else {
            Customer customer = customerRepository.findByAccount(receiverAccount);
            String nickname = customer.getName();
            receiver.setNickName(nickname);
        }

        receiver.setUpdatedAt(LocalDateTime.now());

        receiverRepository.save(receiver);
        return "Update receiver successfully";
    }

    public String deleteReceiver(ReceiverDTO receiverDTO) {
//        Account senderAccount = accountRepository.findByAccountNumber(receiverDTO.getSenderAccountNumber());
//        Account receiverAccount = accountRepository.findByAccountNumber(receiverDTO.getReceiverAccountNumber());
        Receiver receiver = receiverRepository.findBySenderAccountIdAndReceiverAccountId(receiverDTO.getSenderAccountNumber(), receiverDTO.getReceiverAccountNumber());

        if (receiver == null) {
            return "Cannot find sender account and receiver account";
        }

        receiverRepository.delete(receiver);
        return "Delete receiver successfully";
    }
}
