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
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReceiverService {
    private final ReceiverRepository receiverRepository;
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;

    public List<Receiver> getAllReceivers() {
        return receiverRepository.findAll();
    }

    public String createReceiver(ReceiverDTO receiverDTO) {
        Receiver receiver = new Receiver();

        Account senderAccount = accountRepository.findByAccountNumber(receiverDTO.getSenderAccountNumber());
        Account receiverAccount = accountRepository.findByAccountNumber(receiverDTO.getReceiverAccountNumber());

        if (senderAccount == null) {
            return "Cannot find sender account";
        }

        if (receiverAccount == null) {
            return "Cannot find receiver account";
        }

        receiver.setSenderAccountId(senderAccount);
        receiver.setReceiverAccountId(receiverAccount);

        if (receiverDTO.getNickName() != null || !receiverDTO.getNickName().isEmpty()) {
            receiver.setNickName(receiverDTO.getNickName());
        } else {
            Customer customer = customerRepository.findByAccount(receiverAccount);
            String nickname = customer.getName();
            receiver.setNickName(nickname);
        }

        receiver.setCreatedAt(LocalDateTime.now());
        receiver.setUpdatedAt(LocalDateTime.now());

        receiverRepository.save(receiver);
        return "Create receiver successfully";
    }

    public String updateReceiver(ReceiverDTO receiverDTO) {
        Account senderAccount = accountRepository.findByAccountNumber(receiverDTO.getSenderAccountNumber());
        Account receiverAccount = accountRepository.findByAccountNumber(receiverDTO.getReceiverAccountNumber());
        Receiver receiver = receiverRepository.findBySenderAccountIdAndAndReceiverAccountId(senderAccount, receiverAccount);

        if (receiver == null) {
            return "Cannot find sender account and receiver account";
        }

        if (receiverDTO.getNickName() != null || !receiverDTO.getNickName().isEmpty()) {
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
        Account senderAccount = accountRepository.findByAccountNumber(receiverDTO.getSenderAccountNumber());
        Account receiverAccount = accountRepository.findByAccountNumber(receiverDTO.getReceiverAccountNumber());
        Receiver receiver = receiverRepository.findBySenderAccountIdAndAndReceiverAccountId(senderAccount, receiverAccount);

        if (receiver == null) {
            return "Cannot find sender account and receiver account";
        }

        receiverRepository.delete(receiver);
        return "Delete receiver successfully";
    }
}
