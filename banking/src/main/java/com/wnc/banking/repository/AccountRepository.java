package com.wnc.banking.repository;

import com.wnc.banking.entity.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository extends JpaRepository<Account, String> {
    Account findByAccountNumber(String accountNumber);
}
