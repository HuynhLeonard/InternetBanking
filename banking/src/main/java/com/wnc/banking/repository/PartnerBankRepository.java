package com.wnc.banking.repository;

import com.wnc.banking.entity.PartnerBank;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PartnerBankRepository extends JpaRepository<PartnerBank, Long> {
    PartnerBank findPartnerBankById(Long partnerId);
}
