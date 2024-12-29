package com.wnc.banking.repository;

import com.wnc.banking.entity.Account;
import com.wnc.banking.entity.EmployeeTransaction;
import com.wnc.banking.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeTransactionRepository extends JpaRepository<EmployeeTransaction, Integer> {
    List<EmployeeTransaction> findByServiceProvider(ServiceProvider serviceProvider);
    List<EmployeeTransaction> findByReceiverAccount(Account receiverAccount);
}
