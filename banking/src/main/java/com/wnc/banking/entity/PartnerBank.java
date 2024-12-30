package com.wnc.banking.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "bank")
public class PartnerBank {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Integer id;

    @Size(max = 255)
    @NotNull
    @Column(name = "name", nullable = false)
    private String name;

    @Size(max = 255)
    @Column(name = "shortName")
    private String shortName;

    @Size(max = 255)
    @NotNull
    @Column(name = "urlInfo", nullable = false)
    private String urlInfo;

    @Size(max = 255)
    @NotNull
    @Column(name = "urlTransaction", nullable = false)
    private String urlTransaction;

    @Size(max = 255)
    @NotNull
    @Column(name = "localSecretKey", nullable = false)
    private String localSecretKey;

    @Size(max = 255)
    @NotNull
    @Column(name = "foreignSecretKey", nullable = false)
    private String foreignSecretKey;

    @Size(max = 255)
    @NotNull
    @Column(name = "foreignPublicKey", nullable = false)
    private String foreignPublicKey;

    @Column(name = "createdAt")
    private Instant createdAt;

    @Column(name = "updateAt")
    private Instant updateAt;

}