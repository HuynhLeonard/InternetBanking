package com.wnc.banking.repository;

import com.wnc.banking.entity.EmployeeTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeTransactionRepository extends JpaRepository<EmployeeTransaction, Integer> {

}
