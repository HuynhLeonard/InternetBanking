package com.wnc.banking.service;

import com.wnc.banking.controller.TransactionController;
import com.wnc.banking.dto.EmployeeTransactionDTO;
import com.wnc.banking.dto.TransactionDTO;
import com.wnc.banking.dto.TransactionResponse;
import com.wnc.banking.entity.*;
import com.wnc.banking.repository.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
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
    private final ExternalTransactionRepository externalTransactionRepository;

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
                        DeptReminder reminder = deptReminderRepository.findBySenderAccountIdAndReceiverAccountIdAndStatusAndAmount(receiverAccount.getAccountNumber(), senderAccount.getAccountNumber(), "Chưa thanh toán", transaction.getAmount());
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
                    DeptReminder reminder = deptReminderRepository.findBySenderAccountIdAndReceiverAccountIdAndStatusAndAmount(receiverAccount.getAccountNumber(), senderAccount.getAccountNumber(), "Chưa thanh toán", transaction.getAmount());
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

    public List<TransactionResponse> getTransactionByAccount(String accountId) {
       Optional<Account> account = accountRepository.findById(accountId);

       if (account.isPresent()) {
           List<Transaction> sendInternalTransactions = transactionRepository.findBySenderAccountAndType(account.get(),"internal");
           List<TransactionResponse> allSendTransactions = new ArrayList<>();
           for (Transaction transaction : sendInternalTransactions) {
               TransactionResponse transactionResponse = new TransactionResponse();
               transactionResponse.setAmount(transaction.getAmount());
               transactionResponse.setDescription(transaction.getDescription());
               transactionResponse.setType(transaction.getType());
               //
               transactionResponse.setReceiverAccountName(transaction.getReceiverAccount().getCustomer().getName());
               transactionResponse.setReceiverAccountNumber(transaction.getReceiverAccount().getAccountNumber());
               //
               transactionResponse.setSenderAccountName(transaction.getSenderAccount().getCustomer().getName());
               transactionResponse.setSenderAccountNumber(transaction.getSenderAccount().getAccountNumber());
               // adding
               transactionResponse.setCreatedAt(transaction.getCreatedAt());
               allSendTransactions.add(transactionResponse);
           }
           List<Transaction> receiveInternalTransactions = transactionRepository.findByReceiverAccountAndType(account.get(),"internal");
           List<TransactionResponse> allReceiveTransactions = new ArrayList<>();
           for (Transaction transaction : receiveInternalTransactions) {
               TransactionResponse transactionResponse = new TransactionResponse();
               transactionResponse.setAmount(transaction.getAmount());
               transactionResponse.setDescription(transaction.getDescription());
               transactionResponse.setType(transaction.getType());
               //
               transactionResponse.setReceiverAccountName(transaction.getReceiverAccount().getCustomer().getName());
               transactionResponse.setReceiverAccountNumber(transaction.getReceiverAccount().getAccountNumber());
               //
               transactionResponse.setSenderAccountName(transaction.getSenderAccount().getCustomer().getName());
               transactionResponse.setSenderAccountNumber(transaction.getSenderAccount().getAccountNumber());
               // adding
               transactionResponse.setCreatedAt(transaction.getCreatedAt());
               allReceiveTransactions.add(transactionResponse);
           }
           List<Transaction> sendExternalTransactions = transactionRepository.findBySenderAccountAndType(account.get(), "external");
           List<Transaction> receiveExternalTransactions = transactionRepository.findByReceiverAccountAndType(account.get(), "external");

           // Receive debt
           List<TransactionResponse> allReceiveDebtTransactions = new ArrayList<>();
           List<Transaction> debtTransactions = transactionRepository.findByReceiverAccountAndType(account.get(), "dept");
           for (Transaction transaction : debtTransactions) {
               TransactionResponse transactionResponse = new TransactionResponse();
               transactionResponse.setAmount(transaction.getAmount());
               transactionResponse.setDescription(transaction.getDescription());
               transactionResponse.setType(transaction.getType());
               //
               transactionResponse.setReceiverAccountName(transaction.getReceiverAccount().getCustomer().getName());
               transactionResponse.setReceiverAccountNumber(transaction.getReceiverAccount().getAccountNumber());
               //
               transactionResponse.setSenderAccountName(transaction.getSenderAccount().getCustomer().getName());
               transactionResponse.setSenderAccountNumber(transaction.getSenderAccount().getAccountNumber());
               // adding
               transactionResponse.setCreatedAt(transaction.getCreatedAt());
               allReceiveDebtTransactions.add(transactionResponse);
           }
           // Send debt
           List<TransactionResponse> allSendDebtTransactions = new ArrayList<>();
           List<Transaction> debtSendTransactions = transactionRepository.findBySenderAccountAndType(account.get(), "dept");
           for (Transaction transaction : debtSendTransactions) {
               TransactionResponse transactionResponse = new TransactionResponse();
               transactionResponse.setAmount(transaction.getAmount());
               transactionResponse.setDescription(transaction.getDescription());
               transactionResponse.setType(transaction.getType());
               //
               transactionResponse.setReceiverAccountName(transaction.getReceiverAccount().getCustomer().getName());
               transactionResponse.setReceiverAccountNumber(transaction.getReceiverAccount().getAccountNumber());
               //
               transactionResponse.setSenderAccountName(transaction.getSenderAccount().getCustomer().getName());
               transactionResponse.setSenderAccountNumber(transaction.getSenderAccount().getAccountNumber());
               // adding
               transactionResponse.setCreatedAt(transaction.getCreatedAt());
               allSendDebtTransactions.add(transactionResponse);
           }

           // Nhan vien nap tien
           List<TransactionResponse> allResponseEmployeeTransaction = new ArrayList<>();
           List<EmployeeTransaction> allEmployeeTransactions = employeeTransactionRepository.findByReceiverAccount(account.get());
           for(EmployeeTransaction employeeTransaction : allEmployeeTransactions) {
               TransactionResponse transactionResponse = new TransactionResponse();
               transactionResponse.setAmount(employeeTransaction.getAmount());
               transactionResponse.setDescription("Deposit by employee to " + account.get().getCustomer().getName());
               transactionResponse.setType("Employee");
               transactionResponse.setSenderAccountName("Deposit By Employee");
               transactionResponse.setReceiverAccountName(account.get().getCustomer().getName());
               transactionResponse.setReceiverAccountNumber(account.get().getAccountNumber());
               //adding
               transactionResponse.setCreatedAt(employeeTransaction.getCreatedAt());
               allResponseEmployeeTransaction.add(transactionResponse);
           }
           // Lien ngan hang
           List<TransactionResponse> allSenderExternalTransactions = new ArrayList<>();
           List<ExternalTransaction> externalTransactions = externalTransactionRepository.findExternalTransactionByTypeAndAccountNumber("out", account.get().getAccountNumber());
           for(ExternalTransaction externalTransaction : externalTransactions) {
               TransactionResponse transactionResponse = new TransactionResponse();
               transactionResponse.setAmount(externalTransaction.getAmount());
               transactionResponse.setType("external");
               transactionResponse.setDescription(transactionResponse.getDescription());

               transactionResponse.setReceiverAccountName(externalTransaction.getForeignAccountName());
               transactionResponse.setReceiverAccountNumber(externalTransaction.getForeignAccountNumber());

               transactionResponse.setSenderAccountName(account.get().getCustomer().getName());
               transactionResponse.setSenderAccountNumber(account.get().getAccountNumber());

               transactionResponse.setCreatedAt(externalTransaction.getCreatedAt());
               allSenderExternalTransactions.add(transactionResponse);
           }

           List<TransactionResponse> allReceiveExternalTransactions = new ArrayList<>();
           List<ExternalTransaction> receiveExternalTransaction = externalTransactionRepository.findExternalTransactionByTypeAndAccountNumber("in", account.get().getAccountNumber());
           for(ExternalTransaction externalTransaction : receiveExternalTransaction) {
               TransactionResponse transactionResponse = new TransactionResponse();
               transactionResponse.setAmount(externalTransaction.getAmount());
               transactionResponse.setType("external");
               transactionResponse.setDescription(transactionResponse.getDescription());
               transactionResponse.setSenderAccountName(externalTransaction.getForeignAccountName());
               transactionResponse.setSenderAccountNumber(externalTransaction.getForeignAccountNumber());

               transactionResponse.setReceiverAccountName(account.get().getCustomer().getName());
               transactionResponse.setReceiverAccountNumber(account.get().getAccountNumber());

               transactionResponse.setCreatedAt(externalTransaction.getCreatedAt());
               allReceiveExternalTransactions.add(transactionResponse);
           }



           List<TransactionResponse> allTransactions = new ArrayList<>();
           allTransactions.addAll(allSendTransactions);
           allTransactions.addAll(allReceiveTransactions);
           allTransactions.addAll(allReceiveDebtTransactions);
           allTransactions.addAll(allSendDebtTransactions);
           allTransactions.addAll(allResponseEmployeeTransaction);
           allTransactions.addAll(allSenderExternalTransactions);
           allTransactions.addAll(allReceiveExternalTransactions);
           return allTransactions;
       } else {
           return null;
       }
    }

    public List<Transaction> getDeptTransactionByAccount(String accountId) {
        Optional<Account> account = accountRepository.findById(accountId);

        if (account.isPresent()) {
            List<Transaction> sendDeptTransactions = transactionRepository.findBySenderAccountAndType(account.get(),"dept");
            List<Transaction> receiveDeptTransactions = transactionRepository.findByReceiverAccountAndType(account.get(),"dept");

            List<Transaction> allTransactions = new ArrayList<>();
            allTransactions.addAll(sendDeptTransactions);
            allTransactions.addAll(receiveDeptTransactions);
            return allTransactions;
        } else {
            return null;
        }
    }
}
