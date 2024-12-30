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
@Table(name = "external_transaction")
public class ExternalTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    @JsonIgnore
    private Integer id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "bankId", nullable = false)
    @JsonIgnore
    private PartnerBank bank;

    @Size(max = 255)
    @NotNull
    @Column(name = "accountNumber", nullable = false)
    private String accountNumber;

    @NotNull
    @Column(name = "amount", nullable = false)
    private Long amount;

    @Size(max = 255)
    @NotNull
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "createdAt")
    private Instant createdAt;

    @Size(max = 255)
    @NotNull
    @Column(name = "foreignAccountNumber", nullable = false)
    private String foreignAccountNumber;

    @Size(max = 255)
    @NotNull
    @Column(name = "theirSignature", nullable = false)
    @JsonIgnore
    private String theirSignature;
}