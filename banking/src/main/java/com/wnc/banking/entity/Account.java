package com.wnc.banking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.GenericGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "account")
public class Account {
    @Id
    @GeneratedValue(generator = "uuid2")
    @GenericGenerator(name = "uuid2", strategy = "uuid2")
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "accountNumber", nullable = false, unique = true)
    private String accountNumber;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.REMOVE)
    @JoinColumn(name = "customerId", nullable = false)
    private Customer customer;

    @Column(name = "balance", nullable = false)
    private Long balance;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "createdAt")
    private LocalDateTime createdAt;

    @DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
    @Column(name = "updatedAt")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "senderAccountId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Receiver> senders = new LinkedHashSet<>();

    @OneToMany(mappedBy = "receiverAccountId", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Receiver> receivers = new LinkedHashSet<>();

    public Account() {
        this.balance = 100000L;
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
}