package com.wnc.banking.service;

import com.wnc.banking.dto.PartnerGetAccountResponse;
import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.ExternalTransaction;
import com.wnc.banking.entity.PartnerBank;
import com.wnc.banking.entity.Transaction;
import com.wnc.banking.repository.AccountRepository;
//import com.wnc.banking.repository.PartnerBankRepository;
import com.wnc.banking.repository.ExternalTransactionRepository;
import com.wnc.banking.repository.PartnerBankRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.time.Instant;

@Service
public class PartnerService {
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PartnerBankRepository partnerBankRepository;

    @Autowired
    private ExternalTransactionRepository externalTransactionRepository;

    public PartnerGetAccountResponse getAccountInformation(String accountNumber) throws Exception {
        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new Exception("Account not found");
        }

        PartnerGetAccountResponse responseData = new PartnerGetAccountResponse();
        responseData.setBalance(account.getBalance());
        responseData.setCustomerName(account.getCustomer().getName());

        return responseData;
    }

    public PartnerBank getPartnerBank(Long id) throws Exception {
        return partnerBankRepository.findPartnerBankById(id);
    }

    public ExternalTransaction partnerDeposit(String foreignAccountNumber, String accountNumber, PartnerBank partnerBank, Long amount, String theirSignature) throws Exception {
        if (partnerBank == null) {
            throw new Exception("PartnerBank not found");
        }

        Account account = accountRepository.findByAccountNumber(accountNumber);

        if (account == null) {
            throw new Exception("Account not found");
        }

        Long newBalance = account.getBalance() + amount;
        account.setBalance(newBalance);

        ExternalTransaction externalTransaction = new ExternalTransaction();
        externalTransaction.setAmount(amount);
        externalTransaction.setAccountNumber(accountNumber);
        externalTransaction.setBank(partnerBank);
        externalTransaction.setCreatedAt(Instant.now());
        externalTransaction.setType("Receive");
        externalTransaction.setTheirSignature(theirSignature);
        externalTransaction.setForeignAccountNumber(foreignAccountNumber);

        accountRepository.save(account);
        externalTransactionRepository.save(externalTransaction);

        return externalTransaction;
    }
}
