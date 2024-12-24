package com.wnc.banking.repository;

import com.wnc.banking.entity.ServiceProvider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceProviderRepository extends JpaRepository<ServiceProvider, String> {
    @Query("SELECT sp FROM ServiceProvider sp WHERE sp.name <> :excludeName")
    List<ServiceProvider> findByNameNotIn(String excludeName);
    ServiceProvider findByEmail(String email);
}
