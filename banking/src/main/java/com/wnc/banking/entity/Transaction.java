package com.wnc.banking.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "transaction")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "senderAccountId", nullable = false)
    @JsonIgnore
    private Account senderAccount;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "receiverAccountId", nullable = false)
    @JsonIgnore
    private Account receiverAccount;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Long amount;

    @Size(max = 255)
    @Column(name = "description")
    private String description;

    @NotNull
    @Lob
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "createdAt")
    private Instant createdAt;
}