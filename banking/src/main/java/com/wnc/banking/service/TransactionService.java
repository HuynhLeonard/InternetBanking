package com.wnc.banking.service;

import com.wnc.banking.controller.TransactionController;
import com.wnc.banking.dto.EmployeeTransactionDTO;
import com.wnc.banking.dto.TransactionDTO;
import com.wnc.banking.entity.*;
import com.wnc.banking.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class TransactionService {
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final DeptReminderRepository deptReminderRepository;
    private final ServiceProviderRepository serviceProviderRepository;
    private final EmployeeTransactionRepository employeeTransactionRepository;

    public Transaction createTransaction(TransactionDTO transaction) throws Exception {
            String type = transaction.getType();
            // For customer transactions
            if (!type.equals("deposit")) {
                Account senderAccount = accountRepository.findByAccountNumber(transaction.getSenderAccountNumber());
                Account receiverAccount = accountRepository.findByAccountNumber(transaction.getReceiverAccountNumber());
                if (senderAccount == null) {
                    throw new Exception("Sender account not found");
                }

                if (receiverAccount == null) {
                    throw new Exception("Receiver account not found");
                }

                // Check account balance
                if (senderAccount.getBalance() < transaction.getAmount()) {
                    throw new Exception("Account doesn't have enough money to make this transaction");
                }

                // Check transaction type
                if (type.equals("internal") || type.equals("dept")) {
                    if (type.equals("dept")) {
                        DeptReminder reminder = deptReminderRepository.findBySenderAccountIdAndReceiverAccountIdAndStatusAndAmount(receiverAccount, senderAccount, "Chưa thanh toán", transaction.getAmount());
                        if (reminder == null) {
                            throw new Exception("Dept reminder not found");
                        }
                        if (transaction.getAmount() < reminder.getAmount()) {
                            throw new Exception("You must pay: " + reminder.getAmount() + " for this debt");
                        }
                    }

                    senderAccount.setBalance(senderAccount.getBalance() - transaction.getAmount());
                    receiverAccount.setBalance(receiverAccount.getBalance() + transaction.getAmount());
                    accountRepository.save(senderAccount);
                    accountRepository.save(receiverAccount);


                } else if (type.equals("external")) {
                    // TODO: for external bank
                } else {
                    throw new Exception("Invalid transaction type");
                }


                if (type.equals("dept")) {
                    DeptReminder reminder = deptReminderRepository.findBySenderAccountIdAndReceiverAccountIdAndStatusAndAmount(receiverAccount, senderAccount, "Chưa thanh toán", transaction.getAmount());
                    reminder.setStatus("Đã thanh toán");
                    deptReminderRepository.save(reminder);
                }

                Transaction newTransaction = new Transaction();
                newTransaction.setSenderAccount(senderAccount);
                newTransaction.setReceiverAccount(receiverAccount);
                newTransaction.setAmount(transaction.getAmount());
                newTransaction.setDescription(transaction.getDescription());
                newTransaction.setType(transaction.getType());
                newTransaction.setCreatedAt(Instant.now());
                transactionRepository.save(newTransaction);

                return newTransaction;
            } else {
                ServiceProvider employee = serviceProviderRepository.findByEmail(transaction.getSenderAccountNumber());
                if (employee == null) {
                    throw new Exception("Employee not found");
                }
            }

            return null;
    }

    public EmployeeTransaction createEmployeeTransaction(EmployeeTransactionDTO employeeTransaction) throws Exception {
        ServiceProvider provider = serviceProviderRepository.findServiceProviderById(employeeTransaction.getServiceProviderId());
        Account receiver = accountRepository.findByAccountNumber(employeeTransaction.getReceiverAccountNumber());
        if (receiver == null) {
            throw new Exception("Receiver not found");
        }
        if (provider == null) {
            throw new Exception("Provider not found");
        }

        receiver.setBalance(receiver.getBalance() + employeeTransaction.getAmount());
        EmployeeTransaction transaction = new EmployeeTransaction();
        transaction.setReceiverAccount(receiver);
        transaction.setServiceProvider(provider);
        transaction.setAmount(employeeTransaction.getAmount());
        transaction.setCreatedAt(Instant.now());

        accountRepository.save(receiver);
        employeeTransactionRepository.save(transaction);

        return transaction;
    }

    public List<EmployeeTransaction> getAllEmployeeTransaction() {
        return employeeTransactionRepository.findAll();
    }

    public List<EmployeeTransaction> getEmployeeTransactionByServiceProvider(String serviceProviderId) {
        Optional<ServiceProvider> serviceProvider = serviceProviderRepository.findById(serviceProviderId);
        return serviceProvider.map(employeeTransactionRepository::findByServiceProvider).orElse(null);
    }

    public List<EmployeeTransaction> getEmployeeTransactionByReceiverAccount(String receiverAccountId) {
        Optional<Account> receiverAccount = accountRepository.findById(receiverAccountId);
        return receiverAccount.map(employeeTransactionRepository::findByReceiverAccount).orElse(null);
    }
}
