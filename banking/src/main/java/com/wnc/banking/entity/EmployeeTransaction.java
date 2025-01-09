package com.wnc.banking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "employee_transaction")
public class EmployeeTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "serviceProviderId", nullable = false)
    @JsonIgnore
    private ServiceProvider serviceProvider;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiverAccountId", nullable = false)
    @JsonIgnore
    private Account receiverAccount;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Long amount;

    @Column(name = "createdAt")
    private Instant createdAt;
}