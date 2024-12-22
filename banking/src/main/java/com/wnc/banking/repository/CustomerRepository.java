package com.wnc.banking.repository;

import com.wnc.banking.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, String> {
    Customer findByEmail(String email);
    void deleteByEmail(String email);
    boolean existsByEmail(String email);
}
